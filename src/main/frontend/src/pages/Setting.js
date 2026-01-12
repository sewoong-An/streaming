import { useEffect, useRef, useState } from "react";
import { authApi } from "../api/api";
import styles from "styles/page/Setting.module.css";
import Modal from "../components/common/Modal";

function Setting() {
  const [name, setName] = useState("");
  const [handle, setHandle] = useState("");
  const [channelImage, setChannelImage] = useState("");
  const [originImage, setOriginImage] = useState("");
  const fileRef = useRef(null);

  const [modalState, setModalState] = useState(false);

  const [errName, setErrName] = useState("");
  const [errHandle, setErrHandle] = useState("");

  const openModal = () => {
    setModalState(true);
  };

  const closeModal = () => {
    setModalState(false);
  };

  const getInfo = async () => {
    await authApi.get("/api/member/getUserInfo").then((response) => {
      setName(response.data.data.channelName);
      setHandle(response.data.data.channelHandle);
      setChannelImage(response.data.data.channelImage);
      setOriginImage(response.data.data.channelImage);
    });
  };

  const checkName = () => {
    console.log("focus out name");
    authApi.get("/api/member/checkName?name=" + name).then((response) => {
      if (response.data.status === "ERROR") {
        setErrName(response.data.message);
      } else {
        setErrName("");
      }
    });
  };

  const checkHandle = () => {
    console.log("focus out handle");
    authApi.get("/api/member/checkHandle?handle=" + handle).then((response) => {
      if (response.data.status === "ERROR") {
        setErrHandle(response.data.message);
      } else {
        setErrHandle("");
      }
    });
  };

  const changeName = (e) => {
    setName(e.target.value);
  };

  const changeHandle = (e) => {
    setHandle(e.target.value);
  };

  const setFile = () => {
    const file = fileRef.current.files[0];
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onloadend = () => {
      setChannelImage(reader.result);
    };
  };

  const setOrigin = () => {
    setChannelImage(originImage);
    fileRef.current.value = "";
  };

  const update = () => {
    let changeImageUrl = originImage;

    const uploadImage = async () => {
      const form = new FormData();
      form.append("file", fileRef.current.files[0]);
      form.append("origin", originImage);

      if (
        channelImage != null &&
        channelImage != "" &&
        originImage != channelImage
      ) {
        await authApi
          .post("/api/member/uploadChannelImage", form, {
            headers: { "Content-Type": "multipart/form-data" },
          })
          .then((response) => {
            changeImageUrl = response.data.channelImageUrl;
          });
      }
    };

    const updateInfo = async () => {
      await authApi
        .post("/api/member/updateChannelInfo", {
          name: name,
          handle: handle,
          imageUrl: changeImageUrl,
        })
        .then((response) => {
          closeModal();
        });
    };

    uploadImage().then(() => {
      updateInfo().then(() => {
        getInfo().then();
      });
    });
  };

  useEffect(() => {
    getInfo().then();
  }, []);

  return (
    <div className={styles.contentBox}>
      <div className={styles.headerBox}>
        <div className={styles.title}>채널 설정</div>
        <Modal
          open={modalState}
          close={closeModal}
          title={"채널 정보 수정"}
          isCheck={true}
          checkFn={update}
          size={"mini"}
        >
          채널 정보를 수정 하시겠습니까?
        </Modal>
        <button className={styles.saveBtn} onClick={openModal}>
          수정
        </button>
      </div>
      <div className={styles.infoBox}>
        <div className={styles.subject}>채널명</div>
        <div className={styles.itemBox}>
          <input
            type={"text"}
            value={name}
            onChange={changeName}
            spellcheck="false"
            onBlur={checkName}
          />
        </div>
        {errName !== "" ? (
          <div className={styles.errText}>{errName}</div>
        ) : null}
        <div className={styles.subject}>핸들</div>
        <div className={styles.itemBox}>
          <input
            type={"text"}
            value={handle}
            onChange={changeHandle}
            spellcheck="false"
            onBlur={checkHandle}
          />
        </div>
        {errHandle !== "" ? (
          <div className={styles.errText}>{errHandle}</div>
        ) : null}
        <div className={styles.subject}>채널 이미지</div>
        <input
          type={"file"}
          style={{ display: "none" }}
          ref={fileRef}
          onChange={setFile}
        />
        <img className={styles.channelImg} src={channelImage} />
        <div>
          <button className={styles.ImageBtn} onClick={setOrigin}>
            기존 이미지
          </button>
          <button
            className={styles.ImageBtn}
            onClick={() => fileRef.current.click()}
          >
            이미지 수정
          </button>
        </div>
      </div>
    </div>
  );
}

export default Setting;
