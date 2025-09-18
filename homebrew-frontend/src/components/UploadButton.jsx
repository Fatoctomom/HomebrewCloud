import { useRef, useState } from 'react';

export default function UploadButton( {onUpload} ) {
    const inputRef = useRef(null);
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState('');

    const choose = () => inputRef.current?.click();

    const handleFiles = async (files) => {
        setError('');
        if (!files?.length) return;
        setBusy(true);
        try {
            for (const f of files) await onUpload(f);
        } catch (e) {
            setError(e.message || 'Upload failed');
        } finally {
            setBusy(false);
            if (inputRef.current) inputRef.current.value = '';
        }
    };

    return (
        <div style={{border:'1px solid #ddd', padding:'1rem', borderRadius:12}}>
      <p><strong>Upload files</strong></p>
      <div
        onDragOver={(e)=>e.preventDefault()}
        onDrop={(e)=>{ e.preventDefault(); handleFiles(e.dataTransfer.files); }}
        onClick={choose}
        style={{border:'2px dashed #bbb', padding:'1.25rem', borderRadius:12, cursor:'pointer'}}
      >
        Drop files here or click to choose
      </div>
      <input ref={inputRef} type="file" multiple style={{display:'none'}}
             onChange={(e)=>handleFiles(e.target.files)} />
      <div style={{marginTop:8}}>
        {busy && 'Uploading…'}
        {error && <div style={{color:'crimson'}}>{error}</div>}
      </div>
    </div>
    );
}
