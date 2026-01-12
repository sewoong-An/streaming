import {
  Link,
  Outlet,
  useLocation,
  useOutletContext,
  useParams,
} from "react-router-dom";
import { useEffect, useState } from "react";
import { authApi } from "../api/api";
import styles from "styles/page/Channel.module.css";
import * as common from "common";
import Modal from "../components/common/Modal";

function Channel() {
  const pathName = useLocation().pathname;
  const [state, setState] = useState(false);
  const { handle } = useParams();
  const [info, setInfo] = useState({});
  const [isSub, setIsSub] = useState(false);
  const { getSubscribeList, contentsRef } = useOutletContext();

  const tabs = [
    { name: "동영상", path: "" },
    { name: "재생목록", path: "/playlist" },
  ];

  const openModal = () => {
    setState(true);
  };

  const closeModal = () => {
    setState(false);
  };

  const getChannelInfo = async () => {
    await authApi
      .get("/api/member/getChannelInfoByHandle/" + handle)
      .then((response) => {
        setInfo(response.data.data);
        setIsSub(response.data.data.isSubscribe);
      });
  };

  const createSubscribe = async () => {
    await authApi
      .post("/api/subscribe/createSubscribe/" + info.channelCode)
      .then();
  };

  const deleteSubscribe = async () => {
    await authApi
      .delete("/api/subscribe/deleteSubscribe/" + info.channelCode)
      .then();
  };

  const changeSubscribe = () => {
    if (isSub) {
      deleteSubscribe().then(() => {
        getSubscribeList();
        setIsSub(false);
      });
    } else {
      createSubscribe().then(() => {
        getSubscribeList();
        setIsSub(true);
      });
    }
    setState(false);
  };

  useEffect(() => {
    getChannelInfo().then();
  }, [handle]);

  return (
    <div>
      <Modal
        open={state}
        close={closeModal}
        title={isSub ? "구독 해제" : "구독"}
        isCheck={true}
        checkFn={changeSubscribe}
        size={"mini"}
      >
        {isSub ? "구독 해제 하시겠습니까?" : "구독 하시겠습니까?"}
      </Modal>
      <div className={styles.channelBox}>
        <img className={styles.channelImg} src={info.channelImage} />
        <div className={styles.channelInfo}>
          <div className={styles.channelName}>{info.channelName}</div>
          <div className={styles.subCnt}>
            구독자 {common.formatSub(info.channelSubscribeCount)}
          </div>
        </div>
        <button className={styles.subBtn} onClick={openModal}>
          {isSub ? "구독 해제" : "구독"}
        </button>
      </div>
      <div className={styles.tabBox}>
        {tabs.map((tab, index) => (
          <Link to={"/channel/" + handle + tab.path}>
            <button
              className={
                pathName === "/channel/" + handle + tab.path
                  ? styles.tabActive
                  : styles.tab
              }
            >
              {tab.name}
            </button>
          </Link>
        ))}
      </div>
      <Outlet context={{ handle, contentsRef }} />
    </div>
  );
}

export default Channel;
