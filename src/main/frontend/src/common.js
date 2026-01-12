export function formattime(obj) {
  var result;

  let today = new Date();
  // let uploadTime = new Date(year, month - 1, day, hour, min, sec);
  let uploadTime = new Date(obj);

  var diff = Math.floor((today - uploadTime) / 1000);

  var yearToSecond = 12 * 30 * 24 * 60 * 60;
  var monthToSecond = 30 * 24 * 60 * 60;
  var dayToSecond = 24 * 60 * 60;
  var hourToSecond = 60 * 60;
  var minuteToSecond = 60;

  if (diff / yearToSecond >= 1) {
    result = Math.floor(diff / yearToSecond).toString() + "년전";
  } else if (diff / monthToSecond >= 1) {
    result = Math.floor(diff / monthToSecond).toString() + "개월전";
  } else if (diff / dayToSecond >= 1) {
    result = Math.floor(diff / dayToSecond).toString() + "일전";
  } else if (diff / hourToSecond >= 1) {
    result = Math.floor(diff / hourToSecond).toString() + "시간전";
  } else if (diff / minuteToSecond >= 1) {
    result = Math.floor(diff / minuteToSecond).toString() + "분전";
  } else {
    result = diff.toString() + "초전";
  }

  return result;
}

export function formatViews(views) {
  var result = "";
  views = parseFloat(views);

  if (Math.floor(views / 10000) > 0) {
    result = (Math.floor((views / 10000) * 10) / 10).toString() + "만회";
  } else if (Math.floor(views / 1000) > 0) {
    result = (Math.floor((views / 1000) * 10) / 10).toString() + "천회";
  } else {
    result = views.toString() + "회";
  }
  return result;
}

export function formatSub(count) {
  var result = "";
  count = parseFloat(count);

  if (Math.floor(count / 10000) > 0) {
    result = (Math.floor((count / 10000) * 10) / 10).toString() + "만명";
  } else if (Math.floor(count / 1000) > 0) {
    result = (Math.floor((count / 1000) * 10) / 10).toString() + "천명";
  } else {
    result = count.toString() + "명";
  }
  return result;
}

export function getVideoState(obj) {
  var result;
  if (obj == "public") {
    result = "공개";
  } else if (obj == "private") {
    result = "비공개";
  }
  return result;
}

export function isSameDate(date1, date2) {
  if (date1 == null) {
    return false;
  }

  return (
    date1.getFullYear() === date2.getFullYear() &&
    date1.getMonth() === date2.getMonth() &&
    date1.getDate() === date2.getDate()
  );
}

export function formatRunningTime(seconds) {
  var hour, min, sec, result;

  hour = parseInt(seconds / 3600);
  min = parseInt((seconds % 3600) / 60);
  sec = seconds % 60;

  if (hour.toString().length == 1) hour = "0" + hour;

  if (min.toString().length == 1) min = "0" + min;

  if (sec.toString().length == 1) sec = "0" + sec;

  result = hour + " : " + min + " : " + sec;

  if (hour == "00") {
    if (min.substr(0, 1) == "0") {
      result = min.substr(1, 1) + " : " + sec;
    } else {
      result = min + " : " + sec;
    }
  }

  return result;
}

function formatHistoryDate() {}
