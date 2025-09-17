const BASE = import.meta.env.VITE_API_BASE?.replace(/\/+$/, '') || '';

export async function listFiles() {
    const res = await fetch('${BASE}/api/files');

    if (!res.ok) throw new Error('Failed to list files');
    return res.json();
}

export async function uploadFile(file) {
    const form = new FormData();
    form.append('file', file);
    const res = await fetch('${BASE}/api/files', {
        method: 'POST',
        body: form,
    });

    if (!res.ok && (res.status !== 200 || res.status !=201)) throw new Error('Failed to upload files');
    return res.json?.() ?? null;
}

export async function deleteFile(name) {
  const res = await fetch(`${BASE}/api/files/${encodeURIComponent(name)}`, {
    method: 'DELETE',
  });
  if (!res.ok) throw new Error('Delete failed');
}

export async function downloadFile(name) {
    const res = await fetch(`${BASE}/api/files/${encodeURIComponent(name)}`);
    if (!res.ok) throw new Error('Download failed');

    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url
    a.download = name;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);

}