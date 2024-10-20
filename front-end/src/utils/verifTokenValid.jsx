export default async function verifToken(token, role, setUserInfo) {    
    let returnValue = {
      userValid: false,
      userWrongPage: false,
      error: false
    };

    try {
        await fetch('http://localhost:8080/utilisateur/me', {
            method: "POST",
            headers: {Authorization: `Bearer ${token}`}
        })
          .then(async (res) => {
              if (!res.ok) {
                return false
              }
              const data = await res.json();
              setUserInfo(data);              
              
              returnValue = {
                userValid: true,
                userWrongPage: role.includes(data.role),
                error: false
              };
            }
          )

      } catch (err) {        
        returnValue = {
          userValid: false,
          userWrongPage: false,
          error: true
        }
      }

      return returnValue;
}