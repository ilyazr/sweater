import router from "@/router";
// avoid redundant navigation to current location
export function pushForce(nextPath) {
    if (router.currentRoute.fullPath !== nextPath) {
        router.push(nextPath)
    } else {
        console.log(`Same paths: ${router.currentRoute.fullPath} == ${nextPath}`)
    }
}
