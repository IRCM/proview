/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

