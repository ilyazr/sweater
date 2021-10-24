package ru.zakharov.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zakharov.exceptions.ChatException;
import ru.zakharov.models.User;
import ru.zakharov.models.chat.ChatRoom;
import ru.zakharov.repos.ChatRoomRepo;
import ru.zakharov.repos.UserRepo;
import ru.zakharov.utils.PrincipalUtil;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock private UserRepo userRepo;
    @Mock private ChatRoomRepo chatRoomRepo;

    private static MockedStatic<PrincipalUtil> principal;

    @InjectMocks
    private ChatService underTest;

    @BeforeAll
    static void setUp() {
        principal = mockStatic(PrincipalUtil.class);
    }

    @Test
    void test() {
        assertThat(true).isTrue();
    }

    /*@Test
    void shouldReturnChatRoomWithDefaultSize() {
        //given
        Integer expectedCapacity = 2;
        Integer capacity = null;
        Optional<User> userData = Optional.of(new User("spk4", "123"));
        when(userRepo.findById(anyInt()))
                .thenReturn(userData);
        principal.when(PrincipalUtil::getPrincipalId).thenReturn(12);
        //when
        ChatRoom result = underTest.createChatRoom(capacity);
        //then
        verify(chatRoomRepo, times(1)).save(any(ChatRoom.class));
        assertThat(result.getCapacity()).isEqualTo(expectedCapacity);
    }

    @Test
    void shouldThrownAnExceptionWhenChatRoomWithDefaultSize() {
        Integer capacity = 100;
        assertThatThrownBy(() -> underTest.createChatRoom(capacity))
                .isInstanceOf(ChatException.class)
                .hasMessage("Capacity should be from 2 to 10");
        verify(chatRoomRepo, never()).save(any(ChatRoom.class));
    }

    @Test
    void shouldThrowsExceptionWhenCapacityIsOne() {
        //given
        Integer capacity = 1;
        Optional<User> userData = Optional.of(new User("spk4", "123"));
        //when
        assertThatThrownBy(() -> underTest.createChatRoom(capacity))
                .isInstanceOf(ChatException.class)
                .hasMessage("Capacity should be from 2 to 10");
        verify(chatRoomRepo, never()).save(any(ChatRoom.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 7, 8, 9, 10})
    void shouldCreateChatWithGivenCapacity(int capacity) {
        Optional<User> userData = Optional.of(new User("spk4", "123"));
        when(userRepo.findById(anyInt()))
                .thenReturn(userData);
        principal.when(PrincipalUtil::getPrincipalId).thenReturn(1);
        //when
        ChatRoom result = underTest.createChatRoom(capacity);
        //then
        verify(chatRoomRepo, times(1)).save(any(ChatRoom.class));
        assertThat(result.getCapacity()).isBetween(2, 10);
    }*/

    @Test
    void shouldLeaveChatWhenInChat() {
        //given
        ChatRoom test = new ChatRoom();
        principal.when(PrincipalUtil::getPrincipalId).thenReturn(1);
        when(chatRoomRepo.findById(anyInt()))
                .thenReturn(Optional.of(test));
        when(chatRoomRepo.isUserInChatRoom(anyInt(), anyInt()))
                .thenReturn(true);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        //when
        underTest.leaveChatRoom(1);
        //then
        verify(chatRoomRepo, times(1))
                .leaveChat(captor.capture(), captor.capture());
    }

    @Test
    void shouldNotLeaveChatWhenNotInChat() {
        //given
        ChatRoom test = new ChatRoom();
        principal.when(PrincipalUtil::getPrincipalId).thenReturn(1);
        when(chatRoomRepo.findById(anyInt()))
                .thenReturn(Optional.of(test));
        when(chatRoomRepo.isUserInChatRoom(anyInt(), anyInt()))
                .thenReturn(false);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        //when
        assertThatThrownBy(() -> underTest.leaveChatRoom(1))
                .isInstanceOf(ChatException.class)
                        .hasMessageContaining("not a member of");
        //then
        verify(chatRoomRepo, never())
                .leaveChat(captor.capture(), captor.capture());
    }

    @Test
    void shouldThrownChatExceptionWhenUserAlreadyInChat() {
        //given
        ChatRoom chatRoom = new ChatRoom(2);
        int chatId = 1;
        int userId = 2;
        User user = new User("spk4", "123");
        chatRoom.addUserIntoChatRoom(user);
        when(chatRoomRepo.findById(anyInt())).thenReturn(Optional.of(chatRoom));
        when(userRepo.findById(anyInt())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> underTest.addUserIntoChatRoom(chatId, userId))
                .isInstanceOf(ChatException.class)
                .hasMessage(String.format("User with id %d already in this chat", userId));
    }

    @Test
    void shouldNotThrownExceptionBecauseOfExceedCapacityLimits() {
        int chatCapacity = 4;
        int chatId = 1;
        int userId = 2;
        ChatRoom chatRoom = spy(new ChatRoom(chatCapacity));
        User user1 = new User("user1", "123");
        User user2 = new User("user2", "123");
        User user3 = new User("user3", "123");
        chatRoom.getUsers().addAll(List.of(user1, user2, user3));
        User newUser = spy(new User("spk4", "123"));
        when(chatRoomRepo.findById(anyInt())).thenReturn(Optional.of(chatRoom));
        when(userRepo.findById(anyInt())).thenReturn(Optional.of(newUser));

        underTest.addUserIntoChatRoom(chatId, userId);

        assertThatNoException();
        verify(chatRoom, times(1)).addUserIntoChatRoom(newUser);
        verify(newUser, times(1)).addChatRoom(chatRoom);
    }

    @Test
    void shouldThrownExceptionBecauseOfExceedCapacityLimits() {
        int chatCapacity = 3;
        int chatId = 1;
        int userId = 2;
        ChatRoom chatRoom = spy(new ChatRoom(chatCapacity));
        User user1 = new User("user1", "123");
        User user2 = new User("user2", "123");
        User user3 = new User("user3", "123");
        chatRoom.getUsers().addAll(List.of(user1, user2, user3));
        User newUser = spy(new User("spk4", "123"));
        when(chatRoomRepo.findById(anyInt())).thenReturn(Optional.of(chatRoom));
        when(userRepo.findById(anyInt())).thenReturn(Optional.of(newUser));

        assertThatThrownBy(() -> underTest.addUserIntoChatRoom(chatId, userId))
                .isInstanceOf(ChatException.class)
                .hasMessage("Exceeded the maximum number of users in the chat room");

        verify(chatRoom, never()).addUserIntoChatRoom(newUser);
        verify(newUser, never()).addChatRoom(chatRoom);
    }

}
