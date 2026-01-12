import styles from "styles/page/SetPlaylist.module.css";
import { useEffect, useState } from "react";
import { authApi } from "../api/api";

function SetPlaylist({ id, afterFn }) {
  const [title, setTitle] = useState("");
  const [state, setState] = useState("private");

  const changeTitle = (e) => {
    setTitle(e.target.value);
  };

  const changeState = (e) => {
    setState(e.target.value);
  };

  const getPlaylistInfo = async () => {
    await authApi
      .get("/api/playlist/getPlaylistInfo/" + id)
      .then((response) => {
        setTitle(response.data.data.playlistTitle);
        setState(response.data.data.playlistState);
      });
  };

  const add = () => {
    authApi
      .post("/api/playlist/addPlaylist", {
        title: title,
        state: state,
      })
      .then((response) => {
        afterFn();
      });
  };

  const update = () => {
    authApi
      .post("/api/playlist/updatePlaylist/" + id, {
        title: title,
        state: state,
      })
      .then((response) => {
        afterFn();
      });
  };

  useEffect(() => {
    if (id !== null) {
      getPlaylistInfo().then();
    }
  }, []);

  return (
    <div className={styles.addBox}>
      <div className={styles.itemBox}>
        <div>제목</div>
        <textarea
          className={styles.titleBox}
          value={title}
          placeholder={"재생목록 제목 추가"}
          onChange={changeTitle}
        />
      </div>
      <div className={styles.itemBox}>
        <div>상태</div>
        <select
          className={styles.stateBox}
          value={state}
          onChange={changeState}
        >
          <option value={"private"}>비공개</option>
          <option value={"public"}>공개</option>
        </select>
      </div>
      <div>
        <button className={styles.btn} onClick={id === null ? add : update}>
          {id === null ? "생성" : "수정"}
        </button>
      </div>
    </div>
  );
}

export default SetPlaylist;
