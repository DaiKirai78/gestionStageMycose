import React from "react";
import TokenPageContainer from "./tokenPageContainer.jsx";
import {useOutletContext} from "react-router-dom";
import Rapports from "../components/rapportsPage/rapports.jsx";

const RapportsPage = () => {
    const [userInfo, setUserInfo] = useOutletContext();

  return (
      <TokenPageContainer role={["GESTIONNAIRE_STAGE"]} setUserInfo={setUserInfo}>
          <Rapports />
      </TokenPageContainer>
  );
}

export default RapportsPage;