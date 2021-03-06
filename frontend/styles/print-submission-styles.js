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
    #print-submission .section {
      min-width: 55em;
      border-style: solid;
      margin: 1em 0;
      padding: 1em;
    }
    #print-submission .section &gt; div {
      margin: 0.3em 0;
    }
    #print-submission .two-columns {
      height: auto;
      overflow: hidden;
    }
    #print-submission .two-columns span {
      display: inline-block;
    }
    #print-submission .two-columns .left {
      min-width: 25em;
      width: auto;
      overflow: hidden;
    }
    #print-submission .two-columns .right {
      width: 25em;
      margin-left: 2em;
      float: right;
    }
    #print-submission .user-information .label {
      min-width: 5em;
    }
    #print-submission .sample-information .label {
      min-width: 10em;
    }
    #print-submission .sample-information span {
      display: inline-block;
    }
    #print-submission .sample-name-separator {
      margin-right: 0.3em;
    }
    #print-submission .service-information .label {
      vertical-align: top;
      min-width: 15em;
    }
    #print-submission .service-information span {
      display: inline-block;
    }
    #print-submission .solvent-separator {
      margin-right: 0.3em;
    }
    #print-submission .comment .label {
      vertical-align: top;
      min-width: 5em;
    }
    #print-submission .comment span {
      display: inline-block;
      margin: 0;
      padding: 0;
    }
    #print-submission .files table {
      border-collapse: collapse;
    }
    #print-submission .files th, #print-submission .files td {
      border-style: solid;
      border-width: 1px;
      border-color: black;
    }
    #print-submission .files td {
      margin: 0;
      padding: 0;
    }
    #print-submission .plate-information table {
      border-collapse: collapse;
    }
    #print-submission .plate-information th, #print-submission .plate-information td {
      border-style: solid;
      border-width: 0 1px 1px 0;
      border-color: black;
    }
    #print-submission .plate-information tbody tr th:first-child {
      border-width: 0 1px 1px 1px;
    }
    #print-submission .plate-information tbody tr:first-child th {
      border-width: 1px 1px 1px 0;
    }
    #print-submission .plate-information tbody tr:first-child th:first-child {
      border-width: 1px;
    }
    #print-submission .plate-information td {
      width: 102px;
      height: 102px;
      margin: 0;
      padding: 0;
    }
    #print-submission .plate-information td.banned {
      color: white;
      background-color: red;
    }
    #print-submission .plate-information span {
      margin: 1px;
      display: inline-block;
      width: 96px;
      height: 96px;
      word-wrap: break-word;
      overflow: hidden;
    }
  </style> 
  <style media="print">
    #print-submission .section {
      width: 11in;
    }
    #print-submission .plate-information {
      width: 13in;
      transform: rotate(90deg) translateX(2.1in); 
    }
    #print-submission .plate-information td {
      width: 1.05in;
      height: 1.05in;
    }
    #print-submission .plate-information span {
      width: 0.8in;
      height: 0.8in;
    }
    #print-submission .pagebreak {
      page-break-before: always;
    }
  </style> 
 </custom-style>`;

document.head.appendChild($_documentContainer.content);

