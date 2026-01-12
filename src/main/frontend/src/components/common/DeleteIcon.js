import delIcon from "icons/icon-delete.png";
import styles from "styles/common/DeleteIcon.module.css";
function DeleteIcon({ id, delFun, title }) {
  return (
    <div className={styles.box} title={title} onClick={(e) => delFun(e, id)}>
      <img className={styles.icon} src={delIcon} />
    </div>
  );
}

export default DeleteIcon;
