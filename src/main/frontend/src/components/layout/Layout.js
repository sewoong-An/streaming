import Header from "./Header";
import Menu from "./Menu";
import { Outlet, useLocation } from "react-router-dom";
import styles from "styles/layout/Layout.module.css";
import { useEffect, useRef, useState } from "react";
import { authApi } from "../../api/api";
import Footer from "./Footer";

function Layout() {
  const [subscribeList, setSubscribeList] = useState([]);
  const [winSize, setWinSize] = useState(window.innerWidth > 1000);
  const contentsRef = useRef();

  window.onresize = function () {
    if (window.innerWidth > 1000 && !winSize) {
      setWinSize(true);
    } else if (window.innerWidth <= 1000 && winSize) {
      setWinSize(false);
    }
  };

  const getSubscribeList = async () => {
    await authApi.get("/api/subscribe/getSubscribeList").then((response) => {
      setSubscribeList(response.data.data);
    });
  };

  useEffect(() => {
    getSubscribeList().then();
  }, []);

  return (
    <div>
      {winSize ? (
        <div>
          <Header />
          <Menu subscribeList={subscribeList} />
        </div>
      ) : (
        <div>
          <Header size={"mini"} />
          <Footer />
        </div>
      )}
      <div
        className={winSize ? styles.contents : styles.miniContents}
        ref={contentsRef}
      >
        <Outlet context={{ getSubscribeList, contentsRef, winSize }} />
      </div>
    </div>
  );
}

export default Layout;
