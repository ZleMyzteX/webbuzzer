const STORAGE_KEY = "clientId";
const input = document.getElementById("clientIdInput");
const saveButton = document.getElementById("idSaveButton");

function generateClientId() {
  if (crypto.randomUUID) {
    return crypto.randomUUID();
  }
  // No HTTPS -> no crypto :( fallback to custom blabla
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function loadClientId() {
  let clientId = localStorage.getItem(STORAGE_KEY);
  if (!clientId) {
    clientId = generateClientId();
    localStorage.setItem(STORAGE_KEY, clientId);
  }
  return clientId;
}

function saveClientId() {
  const newId = input.value.trim();
  if (!newId) {
    alert("Client ID cannot be empty.");
    return false;
  }
  localStorage.setItem(STORAGE_KEY, newId);
  updateStatus(`ID saved: ${newId}`);
  return true;
}

input.value = loadClientId();