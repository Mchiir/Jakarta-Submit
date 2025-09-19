<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PDF Viewer</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.16.105/pdf.min.js"></script>
    <style>
        canvas { width: 100%; border: 1px solid #ddd; }
        #pdfControls { text-align: center; margin: 10px 0; }
        button { margin: 0 5px; }
    </style>
</head>
<body>
    <div id="pdfControls">
        <button onclick="prevPage()">Previous</button>
        <span>Page: <span id="pageNum">1</span> / <span id="pageCount">?</span></span>
        <button onclick="nextPage()">Next</button>
    </div>
    
    <canvas id="pdfCanvas"></canvas>

    <script>
        var urlParams = new URLSearchParams(window.location.search);
        var fileName = urlParams.get("fileName");
        var pdfUrl = "/file?name=" + encodeURIComponent(fileName);

        var pdfDoc = null,
            pageNum = 1,
            pageRendering = false,
            canvas = document.getElementById('pdfCanvas'),
            ctx = canvas.getContext('2d');

        function renderPage(num) {
            pageRendering = true;
            pdfDoc.getPage(num).then(function(page) {
                var viewport = page.getViewport({ scale: 1.5 });
                canvas.width = viewport.width;
                canvas.height = viewport.height;
                var renderContext = { canvasContext: ctx, viewport: viewport };
                var renderTask = page.render(renderContext);
                renderTask.promise.then(function() {
                    pageRendering = false;
                    document.getElementById('pageNum').textContent = num;
                });
            });
        }

        function prevPage() {
            if (pageNum <= 1) return;
            pageNum--;
            renderPage(pageNum);
        }

        function nextPage() {
            if (pageNum >= pdfDoc.numPages) return;
            pageNum++;
            renderPage(pageNum);
        }

        pdfjsLib.getDocument(pdfUrl).promise.then(function(pdf) {
            pdfDoc = pdf;
            document.getElementById('pageCount').textContent = pdf.numPages;
            renderPage(pageNum);
        });
    </script>
</body>
</html>
