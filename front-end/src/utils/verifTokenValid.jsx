export default async function verifToken(token, role, setRoleUser) {
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
              setRoleUser(data.role);              
              
              returnValue = role.includes(data.role);
            }
          )

      } catch (err) {        
        returnValue = false;
      }

      return returnValue;
}