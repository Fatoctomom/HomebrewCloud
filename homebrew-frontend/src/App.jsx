import { useEffect, useState } from 'react'; 
import { listFiles, uploadFile, deleteFile, downloadFile } from './api';
import UploadButton from './components/UploadButton';
import fileRow from './components/FileRow';

export default function App() {
  // states to hold data, and error and loading flags
    console.log('In here like swimgear\n');
    const {files, setFiles} = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState('');

    const refresh = async () => {
        setErr(''); 
        setLoading(true);
        try {
            const data = await listFiles();
            setFiles(Array.isArray(data) ? data : (data?.files ?? [])); // store the all the files from db on load, this gunna create huge load times lol
        } catch (e) {
            setErr(e.message || 'failed to load');
        } finally {
            setLoading(false);
        }
    };

    // 
    console.log('boutta effect\n');
    useEffect(() => { refresh(); }, []);
    console.log('finished effect\n');

    //event handlers
    const onUpload = async (file) => { await uploadFile(file); 
      await refresh();
     };

    const onDelete = async (name) => { if (confirm(`Delete "${name}"?`)) { //asking for confirmation before deleteing
      await deleteFile(name); 
      await refresh(); 
    } 
  };
    const onDownload = async (name) => { await downloadFile(name); };

  return (
  <div style={{maxWidth:800, margin:'2rem auto', padding:'0 1rem', fontFamily:'system-ui,sans-serif'}}>
    <h1>MiniCloud</h1>
    <p style={{opacity:0.75, marginTop:-8}}>Simple file browser</p>

    <div style={{margin:'1rem 0'}}><UploadButton onUpload={onUpload} /></div>

    <div style={{border:'1px solid #eee', borderRadius:12, padding:'1rem'}}>
      <div style={{display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:8}}>
        <strong>Files</strong>
        <button onClick={refresh}>↻ Refresh</button>
      </div>

      {loading && <div>Loading…</div>}
      {err && <div style={{color:'crimson'}}>{err}</div>}
      {!loading && !err && files.length === 0 && <div style={{opacity:0.7}}>No files yet.</div>}

      {!loading && !err && files.map(f => (
        <FileRow
          key={typeof f === 'string' ? f : (f.name ?? f.filename)}
          file={f}
          onDelete={onDelete}
          onDownload={onDownload}
        />
      ))}
    </div>
  </div>
);
 }
