const BASE = import.meta.env.VITE_API_BASE?.replace(/\/+$/, '') || ''; // holds the url for api requests replaces trailing slashes with ''


// using resource-orientaed REST calls
export async function listFiles() {
    const res = await fetch(`${BASE}/api/files`);
    console.log('Fetching list of files from:', `${BASE}/api/files`);
    console.log('Response:', res.ok, res.status, res.statusText, res.url);
    if (!res.ok) throw new Error('Failed to list files');
    return res.json();
}

export async function getFiles(fileName) {
    const res = await fetch(`${BASE}/api/files/${encodeURIComponent(fileName)}`);

    if (!res.ok) throw new Error('Failed to get files');
    return res.json();
}

export async function uploadFile(file) {
    const form = new FormData(); // forms a key value pair data payload
    form.append('file', file); // {'file', (data of file here)}
    const res = await fetch(`${BASE}/api/files`, {
        method: 'POST',
        body: form,
    });

    if (!res.ok && (res.status !== 200 || res.status !=201)) throw new Error('Failed to upload files');
    return res.json?.() ?? null;
}

export async function deleteFile(fileName) {
  const res = await fetch(`${BASE}/api/files/${encodeURIComponent(fileName)}`, {
    method: 'DELETE',
  });
  if (!res.ok) throw new Error('Delete failed');
}

export async function downloadFile(fileName) {
    const res = await fetch(`${BASE}/api/files/${encodeURIComponent(fileName)}/download`);
    if (!res.ok) throw new Error('Download failed');

    // create a blob url
    const blob = await res.blob(); // blobs are raw immutable streams of data
    const url = URL.createObjectURL(blob);

    //create hidden element a to trigger the download
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);

}