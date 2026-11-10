import express from 'express';
import 'dotenv/config';
import fs from 'fs/promises';
import path from 'path';
import { spawn } from 'child_process';
import ffmpegPath from 'ffmpeg-static';

const app = express();
const PORT = process.env.PORT || 5122;

// resolve folders relative to server/
const ROOT = path.resolve(import.meta.dirname, '..');
const VIDEOS_DIR = path.resolve(ROOT, process.env.VIDEOS_DIR || 'videos');
const RESULTS_DIR = path.resolve(ROOT, process.env.RESULTS_DIR || 'results');

// app.use(express.static('public'));

// publicly host raw videos and finished CSVs
app.use('/videos', express.static(VIDEOS_DIR));
app.use('/results', express.static(RESULTS_DIR));
// file types we treat as videos
const VIDEO_EXTENSIONS = ['.mp4', '.mov', '.avi', '.mkv', '.webm'];

app.use(express.urlencoded({ extended:true }));

// app.get /api/videos -> list of video filenames
app.get('/api/videos', async (req, res) => {
    try {
        const entries = await fs.readdir(VIDEOS_DIR, { withFileTypes: true });
        const videos = entries
            .filter(e => e.isFile())
            .map(e => e.name)
            .filter(name => VIDEO_EXTENSIONS.includes(path.extname(name).toLowerCase()));
        res.status(200).json(videos);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Error reading video directory' });
    }
});
// GET /thumbnail/:filename -> first frame as a JPEG
app.get('/thumbnail/:filename', (req, res) => {
    // block path tricks like ../../secret.txt
    const filename = path.basename(req.params.filename);
    const videoPath = path.join(VIDEOS_DIR, filename);

    const ffmpeg = spawn(ffmpegPath, [
        '-i', videoPath,     // input video
        '-frames:v', '1',    // grab only one frame
        '-f', 'image2',      // output as an image
        '-vcodec', 'mjpeg',  // encoded as JPEG
        'pipe:1'             // write to stdout instead of a file
    ]);

    const chunks = [];
    ffmpeg.stdout.on('data', chunk => chunks.push(chunk));

    ffmpeg.on('close', code => {
        if (code !== 0 || chunks.length === 0) {
            return res.status(500).json({ error: 'Error generating thumbnail' });
        }
        res.status(200).type('image/jpeg').send(Buffer.concat(chunks));
    });

    ffmpeg.on('error', err => {
        console.error(err);
        if (!res.headersSent) res.status(500).json({ error: 'Error generating thumbnail' });
    });
});

// app.use

app.listen(PORT, () => {
    console.log(`Server started! Access in your browser at the URL below.\nhttp://localhost:${PORT}`);
    console.log(`Videos:  ${VIDEOS_DIR}`);
    console.log(`Results: ${RESULTS_DIR}`);
});