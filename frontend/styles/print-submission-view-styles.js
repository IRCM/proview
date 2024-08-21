const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<custom-style> 
  <style>
    #print-submission-view {
      width: 1056px;
      font-size: 16px;
    }
    #print-submission-view h1 {
      font-size: 32px;
      margin: 16px 0;
      padding: 0;
    }
    #print-submission-view h2 {
      font-size: 24px;
      margin: 16px 0;
      padding: 0;
    }
    #print-submission-view h3 {
      font-size: 19px;
      margin: 0 0 16px 0;
      padding: 0;
    }
  </style> 
  <style media="print">
    body {
      color-adjust: exact;
      -webkit-print-color-adjust: exact;
    }
    #view-layout-tabs {
      display: none;
    }
  </style> 
 </custom-style>`;

document.head.appendChild($_documentContainer.content);

