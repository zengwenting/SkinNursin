function getQueryValue(str, key) {
  const reg = new RegExp("(^|&)" + key + "=([^&]*)(&|$)", "i");
  const result = str.match(reg);
  return result ? result[2] : null;
}

export default function routerJumper(opt) {
  const { query = {} } = opt || {};
  if (!query.scene) {
    return;
  }
  const sceneStr = decodeURIComponent(query.scene);
  const action = getQueryValue(sceneStr, "a");
  const inviteCode = getQueryValue(sceneStr, "b");
  if (action === "invite" && inviteCode) {
    console.info("Invite scene ignored in skincare assistant mode.", inviteCode);
  }
}
