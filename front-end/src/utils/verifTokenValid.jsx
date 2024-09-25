export default async function verifToken(token) {
    if (!token) {
        return false;
    }
    

    try {
        fetch('http://localhost:8080/utilisateur/me', {
            method: "POST",
            headers: {Authorization: `Bearer ${token}`}
        })
          .then(async (res) => {
              if (!res.ok) {
                return false;
              }
              const data = await res.json();
              let newUser = {...data, isLoggedIn: true};
              console.log(newUser);
            }
          ).catch(async () => {
            return false;
        })

      } catch (err) {
        return false;
      }

      return true
}