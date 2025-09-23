export default function FileRow({file, onDelete, onDownload}) {
    const name = typeof file === 'string' ? file : (file.fileName ?? null);
    const size = typeof file === 'object' && file.size != null ? file.size : null;

function formatSize(bytes) {
  const k = 1000;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
  const base1000 = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, base1000)).toFixed(2)) + ' ' + sizes[base1000];
}

    return (
    <div style={{
      display:'flex', justifyContent:'space-between', alignItems:'center',
      padding:'0.5rem 0', borderBottom:'1px solid #eee'
    }}>
      <div>
        <div style={{fontWeight:500}}>{name}</div>
        {size != null && <small style={{opacity:0.7}}>{formatSize(size)} </small>}
      </div>
      <div style={{display:'flex', gap:8}}>
        <button onClick={()=>onDownload(name)}>Download</button>
        <button onClick={()=>onDelete(name)} style={{background:'#d33', color:'#fff', border:'none', padding:'6px 10px', borderRadius:6}}>
          Delete
        </button>
      </div>
    </div>
  );
}