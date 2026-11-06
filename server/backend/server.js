import express from 'express';
const app = express();
const PORT = 5122;

app.use(express.static('public'));
app.use(express.urlencoded({ extended:true }));

// app.get
app.get('/api', (req, res) => {
    res.json({
        message: "API under construction. If you're seeing this, messages are being sent successfully from the backend!"
    })
})

// app.use

app.listen(PORT, () => {
    console.log(`Server started! Access in your browser at the URL below.\nhttp://localhost:${PORT}`);
});