import express from 'express';
const app = express();
const PORT = 5122;

app.use(express.static('public'));
app.use(express.urlencoded({ extended:true }));

// app.get

// app.use

app.listen(PORT, () => {
    console.log(`Server started! Access in your browser at the URL below.\nhttp://localhost:${PORT}`);
});