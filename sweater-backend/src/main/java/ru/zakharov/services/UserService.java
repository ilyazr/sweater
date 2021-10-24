package ru.zakharov.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.zakharov.dto.UserDTO;
import ru.zakharov.exceptions.UserNotFoundException;
import ru.zakharov.jwt.JwtUtil;
import ru.zakharov.models.UploadedFile;
import ru.zakharov.models.User;
import ru.zakharov.registration.token.ConfirmationToken;
import ru.zakharov.registration.token.ConfirmationTokenService;
import ru.zakharov.repos.UserRepo;
import ru.zakharov.utils.PrincipalUtil;
import ru.zakharov.utils.TransactionHandler;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private UserRepo userRepo;
    private ConfirmationTokenService confirmationTokenService;
    private OnlineUserService onlineUserService;
    private EntityManager em;
    private TransactionHandler transactionHandler;
    private JwtUtil jwtUtil;
    private LocalFileStorageService fileStorageService;

    public User addUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo
                .findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public User getUserById(int id) throws UsernameNotFoundException {
        return userRepo
                .getUserWithAvatarUri(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public boolean isUserExistsById(int userId) {
        return userRepo.existsById(userId);
    }

    public void ifUserNotExistsByIdThrowUserNotFound(int userId) {
        if (!isUserExistsById(userId))
            throw new UserNotFoundException(userId);
    }

    public UserDTO mapToDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .country(user.getCountry())
                .avatarURI(user.getAvatarURI())
                .dateOfBirth(user.getDateOfBirth().getTime())
                .build();
    }

    public <R extends Collection<UserDTO>> R mapMultipleUsersToDto(Collection<User> collection, R result) {
        collection.forEach(user -> result.add(mapToDto(user)));
        return result;
    }

    public UserDTO getUserWithAvatarUriByUsername(String username) {
        return userRepo.getUserWithAvatarUriByUsername(username)
                .map(this::mapToDto)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public List<UserDTO> getAllUsersWithAvatar() {
        return userRepo.getAllUsersWithAvatar().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public int getUserIdByUsername(String username) {
        return ((User) loadUserByUsername(username)).getId();
    }

    public String signUpUser(User user) {
        if (userRepo.existsByUsernameAndEmail(user.getUsername(), user.getEmail())) {
            throw new IllegalStateException("This username and email already taken");
        }
        if (userRepo.findUserByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException("This username already taken");
        }
        if (userRepo.findUserByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("This email already taken");
        }

        userRepo.save(user);

        ConfirmationToken confirmationToken =
                confirmationTokenService.createConfirmationTokenForUserAndSave(user);

        return confirmationToken.getToken();
    }

    public int enableUser(String email) {
        return userRepo.enableUser(email);
    }

    public User getUserWithAvatarUriById(int principalId) {
        return userRepo
                .getUserWithAvatarUri(principalId)
                .orElseThrow(() -> new UserNotFoundException(principalId));
    }

    public boolean isUserOnline(int userId) {
        return onlineUserService.isUserOnline(userId);
    }

    private String transformColumnName(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else sb.appendCodePoint(c);
        }
        return sb.toString();
    }

    private Timestamp transformDateOfBirthToTimestamp(String propertyValue) {
        return Timestamp.valueOf(String.format("%s 00:00:00", propertyValue));
    }

    public boolean checkIfAlreadyTaken(String propName, String propValue) {
        if (propName.equals("username")) return userRepo.existsByUsername(propValue);
        else if (propName.equals("email")) return userRepo.existsByEmail(propValue);
        else return false;
    }

    private int changeUserData(final int userId, final String propName, final String propValue) {
        IllegalStateException ex1 = null;
        try {
            String processedPropName = transformColumnName(propName);
            Object processedPropValue;
            if (processedPropName.equals("date_of_birth")) {
                processedPropValue = transformDateOfBirthToTimestamp(propValue);
            } else processedPropValue = propValue;

            if (propName.equals("username") || propName.equals("email")) {
                if (checkIfAlreadyTaken(processedPropName, propValue)) {
                    ex1 = new IllegalStateException(String.format("This %s already taken", propName));
                    throw ex1;
                } else {
                    // change name of user directory async
                    if (propName.equals("username")) {
                        fileStorageService
                                .changeNameOfUserDirectoryAndCopyFiles(PrincipalUtil.getPrincipalUsername(), propValue);

                        // change avatar_uri in db
                        User principal = getUserById(PrincipalUtil.getPrincipalId());
                        String newAvatarURI = principal
                                .getAvatarURI()
                                .replace("/" + principal.getUsername() + "/",
                                        "/" + propValue + "/");
                        userRepo.changeAvatarUri(
                                PrincipalUtil.getPrincipalId(),
                                newAvatarURI
                        );
                    }
                }
            }

            String sql = String.format("update users set %s = :val where id = :uid", processedPropName);
            Query query = em.createNativeQuery(sql);
            query.setParameter("val", processedPropValue);
            query.setParameter("uid", userId);
            return transactionHandler.runInTransaction(query::executeUpdate);
        } catch (Exception e) {
            if (ex1 != null) throw ex1;
            else {
                throw new IllegalStateException(String.format(
                        "Error while change user data. Property: %s; value: %s", propName, propValue
                ));
            }
        }
    }

    public String getAvatarURI(int userId) {
        return getUserById(userId).getAvatarURI();
    }

    public UploadedFile changeAvatarUriOfPrincipal(MultipartFile file) {
        UploadedFile avatar = fileStorageService.storeFile(file);
        int rowsAffected = -1;
        if (userRepo.isAvatarSet(PrincipalUtil.getPrincipalId())) {
            rowsAffected =
                    userRepo.changeAvatarUri(PrincipalUtil.getPrincipalId(), avatar.getDownloadURI());
        } else {
            rowsAffected = userRepo.setAvatarUri(PrincipalUtil.getPrincipalId(), avatar.getDownloadURI());
        }
        if (rowsAffected == 0) throw new IllegalStateException("Error while setting new avatar!");
        return avatar;
    }

    public int changeUserDataOfPrincipal(String propertyName, String propertyValue) {
        User principal = PrincipalUtil.getPrincipalAsUser();
        return switch (propertyName) {
            case "username", "firstName", "lastName", "password",
                    "email", "phoneNumber", "country", "dateOfBirth" ->
                    changeUserData(principal.getId(), propertyName, propertyValue);
            default -> -1;
        };
    }

    public String createNewJwtTokenForPrincipal() {
        User principal = getUserById(PrincipalUtil.getPrincipalId());
        return jwtUtil.createJwtTokenWithBasicPrefix(
                        principal.getId(),
                        principal.getUsername(),
                        principal.getAuthorities());
    }

}
