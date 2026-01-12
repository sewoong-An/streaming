import styles from "styles/layout/MenuItem.module.css";
function MenuItem({ menu, channel, isActive }) {
  if (menu !== undefined) {
    return (
      <div
        className={
          isActive
            ? `${styles.eachMenu} ${styles.active}`
            : `${styles.eachMenu}`
        }
      >
        <img className={styles.menuImage} src={menu.icon} />
        <div className={styles.name}>{menu.name}</div>
      </div>
    );
  } else {
    return (
      <div
        className={
          isActive
            ? `${styles.eachMenu} ${styles.active}`
            : `${styles.eachMenu}`
        }
      >
        <img className={styles.channelImage} src={channel.channelImage} />
        <div className={styles.name}>{channel.channelName}</div>
      </div>
    );
  }
}

export default MenuItem;
