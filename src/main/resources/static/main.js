const sendButton = document.getElementById("buzzer");
const reconnectButton = document.getElementById("reconnectButton");
let serverTimeOffset = 0;

window.addEventListener("DOMContentLoaded", async () => {
  input.value = loadClientId();
  await syncServerTime();
  setupWebSocket();
  const lobbies = await fetchLobbies();
  renderLobbies(lobbies);
});
saveButton.addEventListener("click", saveClientId);
sendButton.addEventListener("click", sendTimestamp);
