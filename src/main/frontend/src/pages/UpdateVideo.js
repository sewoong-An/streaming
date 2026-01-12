import styles from "styles/page/UpdateVideo.module.css";
import { useEffect, useRef, useState } from "react";
import { authApi } from "../api/api";

function UpdateVideo({ id, afterSave }) {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [state, setState] = useState("private");
  const [thumbnailUrl, setThumbnailUrl] = useState("");
  const [originThumbnailUrl, setOriginThumbnailUrl] = useState("");
  const [videoUrl, setVideoUrl] = useState("");
  const fileRef = useRef(null);
  const [errorMsg, setErrorMsg] = useState("");

  const changeTitle = (e) => {
    setTitle(e.target.value);
  };

  const changeDescription = (e) => {
    setDescription(e.target.value);
  };

  const changeState = (e) => {
    setState(e.target.value);
  };

  useEffect(() => {
    authApi.get("/api/video/getVideoInfo/" + id).then((response) => {
      const info = response.data.data;
      setTitle(info.title);
      setDescription(info.description);
      setState(info.state);
      setThumbnailUrl(info.thumbnailUrl);
      setVideoUrl(info.videoUrl);
      setOriginThumbnailUrl(info.thumbnailUrl);
    });
  }, []);

  const changeInfo = () => {
    const errorCheck = document.querySelectorAll(`.${styles.errorItemBox}`);

    if (errorCheck.length > 0) {
      setErrorMsg("규칙에 맞게 정보를 입력해 주세요.");
      return;
    }

    let changeThumbnailUrl = originThumbnailUrl;

    const uploadThumbnail = async () => {
      const form = new FormData();
      form.append("file", fileRef.current.files[0]);
      form.append("origin", originThumbnailUrl);

      if (
        thumbnailUrl != null &&
        thumbnailUrl != "" &&
        originThumbnailUrl != thumbnailUrl
      ) {
        await authApi
          .post("/api/video/uploadThumbnail", form, {
            headers: { "Content-Type": "multipart/form-data" },
          })
          .then((response) => {
            changeThumbnailUrl = response.data.thumbnailUrl;
          });
      }
    };

    const updateInfo = async () => {
      await authApi
        .post("/api/video/updateVideo/" + id, {
          title: title,
          description: description,
          state: state,
          thumbnailUrl: changeThumbnailUrl,
        })
        .then((response) => {
          afterSave();
        });
    };

    uploadThumbnail().then(() => {
      updateInfo().then();
    });
  };

  const setOrigin = () => {
    setThumbnailUrl(originThumbnailUrl);
    fileRef.current.value = "";
  };

  const setFile = () => {
    const file = fileRef.current.files[0];
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onloadend = () => {
      setThumbnailUrl(reader.result);
    };
  };

  return (
    <div>
      <div className={styles.updateBox}>
        <div className={styles.dataSection}>
          <div
            className={
              title != null && title.length > 100
                ? styles.errorItemBox
                : styles.itemBox
            }
          >
            <div>제목</div>
            <textarea
              className={styles.titleBox}
              value={title}
              placeholder={"동영상 제목 추가"}
              onChange={changeTitle}
            />
            <div
              className={
                title != null && title.length > 100
                  ? styles.errorTextLength
                  : styles.textLength
              }
            >
              ({title != null ? title.length : 0}/100)
            </div>
          </div>
          <div
            className={
              description != null && description.length > 5000
                ? styles.errorItemBox
                : styles.itemBox
            }
          >
            <div>설명</div>
            <textarea
              className={styles.descriptionBox}
              value={description}
              placeholder={"동영상 설명 추가"}
              onChange={changeDescription}
            />
            <div
              className={
                description != null && description.length > 5000
                  ? styles.errorTextLength
                  : styles.textLength
              }
            >
              ({description != null ? description.length : 0}/5000)
            </div>
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
        </div>
        <div className={styles.dataSection}>
          <div className={styles.videoBox}>
            {videoUrl != "" ? (
              <video controls controlsList={"nodownload"} id={"item"}>
                <source type="video/mp4" src={videoUrl} />
              </video>
            ) : null}
          </div>
          <div className={styles.subject}>썸네일 이미지</div>
          <input
            type={"file"}
            style={{ display: "none" }}
            ref={fileRef}
            onChange={setFile}
          />
          <div className={styles.thumbnailBox}>
            {thumbnailUrl != null && thumbnailUrl != "" ? (
              <img className={styles.thumbnail} src={thumbnailUrl} />
            ) : (
              <div className={styles.uploadText}>
                파일 선택하여 썸네일 업로드
              </div>
            )}
          </div>
          <div className={styles.subject}>
            <button className={styles.btn} onClick={setOrigin}>
              기존 썸네일
            </button>
            <button
              className={styles.btn}
              onClick={() => fileRef.current.click()}
            >
              썸네일 수정
            </button>
          </div>
        </div>
      </div>
      <div>
        <span className={styles.errorMsg}>{errorMsg}</span>
        <button className={styles.btn} onClick={changeInfo}>
          수정
        </button>
      </div>
    </div>
  );
}

export default UpdateVideo;
