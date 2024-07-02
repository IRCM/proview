const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<custom-style> 
  <style>
    body {
      width: 11in;
    }
    #print-submission-view h1 {
      font-size: 2em;
      margin: 1em 0;
      padding: 0;
    }
    #print-submission-view h2 {
      font-size: 1.5em;
      margin: 1em 0;
      padding: 0;
    }
    #print-submission-view h3 {
      font-size: 1.2em;
      margin: 0 0 1em 0;
      padding: 0;
    }
  </style> 
  <style media="print">
    body {
      color-adjust: exact;
      -webkit-print-color-adjust: exact;
      width: 11in;
    }
    #view-layout-tabs {
      display: none;
    }
  </style> 
 </custom-style>`;

document.head.appendChild($_documentContainer.content);

