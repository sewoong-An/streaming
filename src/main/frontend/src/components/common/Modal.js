import styles from "styles/common/Modal.module.css";
import { useRef } from "react";

function Modal({ open, close, title, children, isCheck, checkFn, size }) {
  const outside = useRef();

  return (
    // 모달이 열릴때 openModal 클래스가 생성된다.
    <div
      className={open ? `${styles.openModal} ${styles.modal}` : styles.modal}
      ref={outside}
      onClick={(e) => {
        if (e.target == outside.current) close();
      }}
    >
      {open ? (
        <section
          className={
            size === "mini"
              ? styles.miniSection
              : size === "tiny"
              ? styles.tinySection
              : styles.section
          }
        >
          <header>
            {title}
            <button onClick={close}>&times;</button>
          </header>
          <main>{children}</main>
          {isCheck === true ? (
            <footer>
              <button onClick={checkFn}>확인</button>
              <button onClick={close}>취소</button>
            </footer>
          ) : null}
        </section>
      ) : null}
    </div>
  );
}

export default Modal;
