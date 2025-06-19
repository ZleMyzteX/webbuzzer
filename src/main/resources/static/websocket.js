let ws = null;

function setupWebSocket() {
  if (ws && ws.readyState === WebSocket.OPEN) {
    updateStatus("Already connected.");
    return;
  }

  ws = new WebSocket(`ws://${location.host}/timestamps`);

  ws.onopen = () => updateStatus("WebSocket connected.");
  ws.onerror = () => updateStatus("WebSocket error. :(");
  ws.onclose = () => updateStatus("WebSocket disconnected.");
}

function sendTimestamp() {
  const clientId = input.value.trim();
  if (!clientId) {
    updateStatus("Client ID cannot be empty.");
    return;
  }

  const timestampNs = getAccurateTimestampNs();
  const message = JSON.stringify(
      {clientId, timestampNs: timestampNs.toString()});

  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(message);
    updateStatus(`Sent timestamp for ID ${clientId}`);
  } else {
    updateStatus("Cannot send - WebSocket not connected.");
  }
}

// Get accurate timestamp in nanoseconds, synchronized to server
function getAccurateTimestampNs() {
  return BigInt(
      Math.floor(performance.timeOrigin + performance.now() + serverTimeOffset)
      * 1_000_000);
}

reconnectButton.addEventListener("click", () => {
  if (ws) {
    ws.close();
  }
  updateStatus("Reconnecting...");
  setupWebSocket();
  syncServerTime();
});