export default async function verifToken(token, role, setUserInfo) {
    if (!token) {
        return false;
    }
    
    let returnValue = false;

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
              
              returnValue = role.includes(data.role);
            }
          )

      } catch (err) {        
        returnValue = false;
      }

      return returnValue;
}