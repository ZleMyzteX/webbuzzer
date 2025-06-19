async function syncServerTime() {
  console.log("Synchronizing server time");
  const t0 = performance.now();
  const response = await fetch("/api/server-time");
  const serverTime = await response.json(); // { now: <ms since epoch> }
  const t1 = performance.now();
  const rtt = (t1 - t0) / 2;
  serverTimeOffset = serverTime.now - (performance.timeOrigin + t0 + rtt);
  console.log('offset: ', serverTimeOffset);
  updateStatus("Server time synchronized.");
}