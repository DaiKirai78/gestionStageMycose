export default function verifToken(navigate, token) {
    if (!token) {
        navigate("/");
        return false;
    }

    try {
        fetch('http://localhost:8080/utilisateur/me', {
            method: "POST",
            headers: {Authorization: `Bearer ${token}`}
        })
          .then(async (res) => {
              if (!res.ok) {
                navigate("/");
                return false
              }
              const data = await res.json();
              let newUser = {...data, isLoggedIn: true};
              console.log(newUser);
              return true
              
            }
          ).catch(async (err) => {
            navigate('/');
            return false;
        })

      } catch (err) {
        navigate('/');
        return false;
      }
}