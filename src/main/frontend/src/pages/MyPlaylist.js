import styles from "styles/page/MyPlaylist.module.css";
import Modal from "../components/common/Modal";
import { useEffect, useState } from "react";
import SetPlaylist from "./SetPlaylist";
import * as common from "../common";
import Pagination from "../components/common/Pagination";
import { authApi } from "../api/api";

function MyPlaylist() {
  const [state, setState] = useState(false);
  const [delModal, setDelModal] = useState(false);
  const [checkCount, setCheckCount] = useState(0);
  const [playlist, setPlaylist] = useState([]);
  const [page, setPage] = useState(1);
  const offset = (page - 1) * 10;

  const [selected, setSelected] = useState(null);

  const openModal = () => {
    setState(true);
  };

  const openDelModal = () => {
    const checkedCount = document.querySelectorAll(
      'input[name="playlist"]:checked'
    ).length;
    if (checkedCount === 0) {
      alert("선택된 재생목록이 없습니다.");
      return;
    }
    setCheckCount(checkedCount);
    setDelModal(true);
  };

  const closeDelModal = () => {
    setDelModal(false);
  };

  const openUpdateModal = (id) => {
    setSelected(id);
    setState(true);
  };

  const closeModal = () => {
    setState(false);
    setSelected(null);
    getPlayList().then();
  };

  const selectAll = (e) => {
    const checkboxes = document.getElementsByName("playlist");

    checkboxes.forEach((checkbox) => {
      checkbox.checked = e.target.checked;
    });
  };

  const getPlayList = async () => {
    await authApi.get("/api/playlist/getPlaylist").then((response) => {
      setPlaylist(response.data.data);
    });
  };

  const deletePlaylists = async () => {
    const playlists = document.querySelectorAll(
      'input[name="playlist"]:checked'
    );

    const ids = [];
    playlists.forEach((r) => ids.push(r.value));
    await authApi
      .delete("/api/playlist/deletePlaylists", { data: { playlistIds: ids } })
      .then(() => {
        closeDelModal();
        getPlayList();
        setCheckCount(0);
      });
  };

  useEffect(() => {
    getPlayList().then();
  }, []);

  return (
    <div>
      <div className={styles.headerBox}>
        <div className={styles.pageTitle}>재생목록</div>
        <button className={styles.addBtn} onClick={openModal}>
          재생목록 생성
        </button>
        <Modal
          open={state}
          close={closeModal}
          title={selected === null ? "재생목록 생성" : "재생목록 수정"}
          size={"mini"}
        >
          <SetPlaylist id={selected} afterFn={closeModal} />
        </Modal>
      </div>
      <div className={styles.selectAction}>
        <button className={styles.deleteBtn} onClick={openDelModal}>
          삭제
        </button>
        <Modal
          open={delModal}
          close={closeDelModal}
          title={"재생목록 삭제"}
          isCheck={true}
          checkFn={deletePlaylists}
          size={"mini"}
        >
          총 {checkCount}개의 재생목록을 삭제 하시겠습니까?
        </Modal>
      </div>
      <table className={styles.table}>
        <colgroup>
          <col width="5%" />
          <col width="50%" />
          <col width="15%" />
          <col width="15%" />
          <col width="15%" />
        </colgroup>
        <thead>
          <tr>
            <th className={styles.checkBoxCol}>
              <input type="checkbox" id="allCheck" onClick={selectAll} />
              <label
                htmlFor="allCheck"
                className={styles.checkBoxLabel}
              ></label>
            </th>
            <th>재생목록</th>
            <th>공개상태</th>
            <th>업데이트 날짜</th>
            <th>동영상 개수</th>
          </tr>
        </thead>
        <tbody>
          {playlist.slice(offset, offset + 10).map((item, index) => (
            <tr className={styles.row} key={item.playlistId}>
              <td className={styles.checkBoxCol}>
                <input
                  type="checkbox"
                  id={`checkBox_playlist_${index}`}
                  name={"playlist"}
                  value={item.playlistId}
                />
                <label
                  htmlFor={`checkBox_playlist_${index}`}
                  className={styles.checkBoxLabel}
                ></label>
              </td>
              <td
                className={styles.imageBox}
                onClick={() => {
                  openUpdateModal(item.playlistId);
                }}
              >
                {item.playlistImageUrl != null &&
                item.playlistImageUrl != "" ? (
                  <img className={styles.image} src={item.playlistImageUrl} />
                ) : (
                  <div className={styles.noImage}>동영상 없음</div>
                )}
                <div className={styles.title}>{item.playlistTitle}</div>
              </td>
              <td>{common.getVideoState(item.playlistState)}</td>
              <td>{item.playlistUpdateDt}</td>
              <td>{item.playlistVideoCount}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <footer>
        <Pagination
          total={playlist.length}
          limit={10}
          page={page}
          setPage={setPage}
        />
      </footer>
    </div>
  );
}

export default MyPlaylist;
