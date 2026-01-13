import styles from "styles/page/MyVideos.module.css";
import { useEffect, useState } from "react";
import Modal from "components/common/Modal";
import UploadVideo from "pages/UploadVideo";
import UpdateVideo from "pages/UpdateVideo";
import Pagination from "components/common/Pagination";
import { authApi } from "../api/api";
import * as common from "common.js";

function MyVideos() {
  const [state, setState] = useState(false);
  const [delModal, setDelModal] = useState(false);
  const [checkCount, setCheckCount] = useState(0);
  const [type, setType] = useState("upload");
  const [videoId, setVideoId] = useState("");
  const [videos, setVideos] = useState([]);
  const [page, setPage] = useState(1);
  const offset = (page - 1) * 10;

  const openModal = () => {
    setState(true);
  };

  const closeModal = () => {
    setState(false);
    setType("upload");
    getMyVideos();
  };

  const openDelModal = () => {
    const checkedCount = document.querySelectorAll(
      'input[name="video"]:checked'
    ).length;
    if (checkedCount === 0) {
      alert("선택된 동영상이 없습니다.");
      return;
    }
    setCheckCount(checkedCount);
    setDelModal(true);
  };

  const closeDelModal = () => {
    setDelModal(false);
  };

  const openUpdateModal = (id) => {
    openModal();
    setVideoId(id);
    setType("update");
  };

  const getMyVideos = () => {
    authApi.get("/api/video/getMyVideos").then((response) => {
      setVideos(response.data.data);
    });
  };

  const deleteVideos = async () => {
    const videos = document.querySelectorAll('input[name="video"]:checked');

    const ids = [];
    videos.forEach((r) => ids.push(r.value));
    await authApi
      .delete("/api/video/deleteVideos", { data: { videoIds: ids } })
      .then(() => {
        closeDelModal();
        getMyVideos();
        setCheckCount(0);
      });
  };

  useEffect(() => {
    getMyVideos();
  }, []);

  const selectAll = (e) => {
    const checkboxes = document.getElementsByName("video");

    checkboxes.forEach((checkbox) => {
      checkbox.checked = e.target.checked;
    });
  };

  const noUpload = () => {
    alert("클라우드 용량 문제로 업로드 제한");
  };

  return (
    <div className={styles.videoList}>
      <div className={styles.uploadBox}>
        <div className={styles.pageTitle}>동영상 관리</div>
        <button className={styles.uploadBtn} onClick={openModal}>
          동영상 업로드
        </button>
        <Modal
          open={state}
          close={closeModal}
          title={type == "upload" ? "동영상 업로드" : "동영상 수정"}
        >
          {type == "upload" ? (
            <UploadVideo update={openUpdateModal} />
          ) : (
            <UpdateVideo id={videoId} afterSave={closeModal} />
          )}
        </Modal>
      </div>
      <div className={styles.selectAction}>
        <button className={styles.deleteBtn} onClick={openDelModal}>
          삭제
        </button>
        <Modal
          open={delModal}
          close={closeDelModal}
          title={"동영상 삭제"}
          isCheck={true}
          checkFn={deleteVideos}
          size={"mini"}
        >
          총 {checkCount}개의 동영상을 삭제 하시겠습니까?
        </Modal>
      </div>
      <table className={styles.videoTable}>
        <colgroup>
          <col width="5%" />
          <col width="60%" />
          <col width="10%" />
          <col width="15%" />
          <col width="10%" />
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
            <th>동영상</th>
            <th>공개상태</th>
            <th>날짜</th>
            <th>조회수</th>
          </tr>
        </thead>
        <tbody>
          {videos.slice(offset, offset + 10).map((video, index) => (
            <tr className={styles.videoRow} key={video.videoId}>
              <td className={styles.checkBoxCol}>
                <input
                  type="checkbox"
                  id={`checkBox_video_${index}`}
                  name={"video"}
                  value={video.videoId}
                />
                <label
                  htmlFor={`checkBox_video_${index}`}
                  className={styles.checkBoxLabel}
                ></label>
              </td>
              <td
                className={styles.videoBox}
                onClick={() => {
                  openUpdateModal(video.videoId);
                }}
              >
                <img className={styles.video} src={video.thumbnailUrl} />
                <div className={styles.TitleAndDescription}>
                  <div className={styles.videoTitle}>{video.title}</div>
                  <div className={styles.videoDescription}>
                    {video.description}
                  </div>
                </div>
              </td>
              <td>{common.getVideoState(video.state)}</td>
              <td>{video.createdDt}</td>
              <td>{video.views}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <footer>
        <Pagination
          total={videos.length}
          limit={10}
          page={page}
          setPage={setPage}
        />
      </footer>
    </div>
  );
}

export default MyVideos;
