function createCORSRequest(method, url, timeout) {
	let xhr = new XMLHttpRequest();
	if ('withCredentials' in xhr) {
		xhr.open(method, url, true);
	} else if (typeof XDomainRequest !== 'undefined') {
		xhr = new XDomainRequest();
		xhr.open(method, url);
	} else {
		xhr = null;
	}
	if (xhr) {
		xhr.timeout = timeout;
	}
	return xhr;
}

function sendRequest(method, url, data, headers, callback, error, times) {
	let xhr = createCORSRequest(method, url, 2500);
	xhr.onreadystatechange = () => {
		if (xhr.readyState == 4 && xhr.status == 200) {
			callback(xhr.responseText);
		}
	};
	xhr.timeout = xhr.onerror = () => {
		if (!times) {
			times = 0;
		}
		console.log({
			url: url,
			data: data,
			times: times
		})
		if (times < 1) {
			sendRequest(method, url, data, headers, callback, error, times + 1);
		} else if (typeof error === 'function') {
			error();
		}
	}
	if (headers) {
		for (key in headers) {
			if (headers.hasOwnProperty(key)) {
				xhr.setRequestHeader(key, headers[key]);
			}
		}
	}
	if (data) {
		xhr.send(data);
	} else {
		xhr.send();
	}
}

function renderPage(data, cache) {
	let files;
	if (data) {
		files = JSON.parse(data);
		window.fileCache.set(files.parent, files);
		preCache(files, 0);
	} else {
		files = cache;
	}
	if (files.parent === window.backFordwardCache.current) {
		renderPath(files.parent);
		if (files.encrypted) {
			handleEncryptedFolder(files);
		} else {
			renderFileList(files);
		}
		renderTreeNode(files);
	}
	document.querySelector('.loading-wrapper').style.display = 'none';

	//树结构和列表都加载完隐藏
	$('#loading_all').remove();
}

function renderPath(path) {
	const createPathSpan = (text, path) => {
		let pathSpan = document.createElement('span');
		pathSpan.innerHTML = text.length > 20 ? text.substring(0, 20) + '..' : text;
		pathSpan.className = text === '/' ? 'nav-arr' : 'nav-path';
		if (path) {
			addPathListener(pathSpan, path);
		}
		return pathSpan;
	};

	const paths = path.split('/');
	let pathSpanWrapper = document.getElementById('path');
	pathSpanWrapper.innerHTML = '';
	pathSpanWrapper.appendChild(createPathSpan(window.api.root));
	let continualPath = '/';
	for (let i = 1; i < paths.length - 1; i++) {
		continualPath += paths[i];
		pathSpanWrapper.appendChild(createPathSpan(paths[i], continualPath));
		pathSpanWrapper.appendChild(createPathSpan('/'));
		continualPath += '/';
	}
	pathSpanWrapper.appendChild(createPathSpan(paths[paths.length - 1]));
}

function renderFileList(files) {
	switchRightDisplay();

	const createFileWrapper = (type, name, time, size, path, url) => {
		let fileWrapper = document.getElementById('file-wrapper-templete').content.cloneNode(true);
		fileWrapper.querySelector('ion-icon').setAttribute('name', type);
		fileWrapper.querySelector('.name').innerHTML = name;
		fileWrapper.querySelector('.time').innerHTML = time;
		fileWrapper.querySelector('.size').innerHTML = size;
		addFileListLineListener(fileWrapper.querySelector('.row.file-wrapper'), path, url, size);
		return fileWrapper;
	};

	const formatDate = date => {
		const addZero = num => num > 9 ? num : '0' + num;
		date = new Date(date);
		const year = date.getFullYear();
		const month = addZero(date.getMonth() + 1);
		const day = addZero(date.getDate());
		const hour = addZero(date.getHours());
		const minute = addZero(date.getMinutes());
		const second = addZero(date.getSeconds());
		return 'yyyy-MM-dd HH:mm:ss'
			.replace('yyyy', year)
			.replace('MM', month)
			.replace('dd', day)
			.replace('HH', hour)
			.replace('mm', minute)
			.replace('ss', second);
	};

	const formatSize = size => {
		let count = 0;
		while (size >= 1024) {
			size /= 1024;
			count++;
		}
		size = size.toFixed(2);
		switch (count) {
			case 1:
				size += ' KB';
				break;
			case 2:
				size += ' MB';
				break;
			case 3:
				size += ' GB';
				break;
			case 4:
				size += ' TB';
				break;
			case 5:
				size += ' PB';
				break;
			default:
				size += ' B';
		}
		return size;
	};

	let fileList = document.getElementById('file-list');
	fileList.innerHTML = '';
	files.items.forEach(file => {
		if (file.name.split('.').pop() === 'md') {
			if (file.url) {
				renderMarkdown(files.parent + (files.parent === "/" ? "" : "/") + file.name,file.url);
			}
		}
		if (file.name !== "README.md") {
			const parent = files.parent === window.api.root ? "" : files.parent;
			fileList.appendChild(
				createFileWrapper(
					file.url ? "document" : "folder",
					file.name,
					formatDate(file.time),
					formatSize(file.size),
					parent + "/" + file.name,
					file.url
				)
			);
		}
	});
}

async function renderTreeNode(files) {
	const createTreeNodeWrapper = (array, type, name, path) => {
		let treeNodeWrapper = document.getElementById('tree-node-wrapper-template').content
			.cloneNode(true);
		let icons = treeNodeWrapper.querySelectorAll('ion-icon');
		icons[0].setAttribute('name', array);
		icons[1].setAttribute('name', type);
		treeNodeWrapper.querySelector('.tree-node-name').innerText = name;
		treeNodeWrapper.appendNode = node => treeNodeWrapper.querySelector('.tree-node-wrapper').append(
			node);
		addTreeNodeListener(treeNodeWrapper.querySelector('.tree-node'), path);
		return treeNodeWrapper;
	}

	const paths = files.parent.split('/');
	let absolutePath = max => {
		let absolutePath = '';
		for (let j = 1; j <= max; j++) {
			absolutePath += '/' + paths[j];
		}
		return absolutePath;
	};
	let maxIndex = paths.length - 1;
	let currentTreeNode = createTreeNodeWrapper('arrow-dropdown',
		'folder-open',
		paths[maxIndex],
		absolutePath(maxIndex)
	);
	files.files.forEach(file => {
		if (!file.url) {
			currentTreeNode.appendNode(createTreeNodeWrapper('arrow-dropright',
				'folder',
				file.name,
				files.parent + '/' + file.name
			));
		}
	});

	for (let i = maxIndex - 1; i > 0; i--) {
		const currentTreeNodeParentAbsolutePath = absolutePath(i);
		let currentTreeNodeParent = createTreeNodeWrapper('arrow-dropdown',
			'folder',
			paths[i],
			currentTreeNodeParentAbsolutePath
		);
		let cache = window.fileCache.get(currentTreeNodeParentAbsolutePath);
		if (cache) {
			cache.files.forEach(file => {
				if (!file.url) {
					if (file.name === paths[i + 1]) {
						currentTreeNodeParent.appendNode(currentTreeNode);
					} else {
						currentTreeNodeParent.appendNode(createTreeNodeWrapper(
							'arrow-dropright',
							'folder',
							file.name,
							currentTreeNodeParentAbsolutePath + '/' + file.name
						));
					}
				}
			});
		} else {
			currentTreeNodeParent.appendNode(currentTreeNode);
		}
		currentTreeNode = currentTreeNodeParent;
	}

	const treeRoot = document.getElementById('tree-root');
	treeRoot.innerHTML = '';
	const cache = window.fileCache.get(window.api.root);
	const currentNodeName = currentTreeNode.querySelector('.tree-node-name').innerText;
	if (cache) {
		cache.files.forEach(file => {
			if (!file.url) {
				if (file.name === currentNodeName) {
					treeRoot.append(currentTreeNode);
				} else {
					treeRoot.append(createTreeNodeWrapper(
						'arrow-dropright',
						'folder',
						file.name,
						window.api.root + file.name
					));
				}
			}
		});
	} else {
		treeRoot.append(currentTreeNode);
	}
}

async function renderMarkdown(path, url) {
	const render = text => {
		let markedText;
		try {
			markedText = marked(text, {
				gfm: true,
				highlight: (code, lang, callback) => {
					return hljs.highlight(lang, code).value;
				}
			});
		} catch (e) {
			markedText = marked(text, {
				gfm: true,
				highlight: (code, lang, callback) => {
					return hljs.highlight('bash', code).value;
				}
			});
		}
		if (window.backFordwardCache.current +
			(window.backFordwardCache.current === "/" ? "" : "/") + "README.md" === path) {
			if (!window.backFordwardCache.preview) {
				document.getElementById('readme').innerHTML = markedText;
				document.querySelector('.markdown-body').style.display = 'block';
			}
		}else if (window.backFordwardCache.preview) {
			let mdDiv = document.createElement("div");
			mdDiv.classList.add("markdown-body");
			mdDiv.innerHTML = markedText;
			document.querySelector(".content").append(mdDiv);
		}
		let cache = window.fileCache.get(path);
		if (!cache || cache === true) {
			window.fileCache.set(path, text);
		}
	};
	let text = window.fileCache.get(path);
	if (text === true) {
		setTimeout(() => renderMarkdown(path, url), 200);
	} else if (text) {
		render(text, path);
	} else {
		window.fileCache.set(path, true);
		sendRequest('GET', url, null, null, text => render(text, path), () => window.fileCache.set(path, false));
	}
}

function handleEncryptedFolder(files) {
	switchRightDisplay('encrypted');
	const password = document.querySelector('.password');
	const input = password.querySelector('input');
	const button = password.querySelector('button');
	const buttonParent = button.parentElement;
	const buttonClone = button.cloneNode(true);
	buttonParent.replaceChild(buttonClone, button);
	input.placeholder = '请输入密码';
	buttonClone.addEventListener('click', event => {
		const passwd = input.value;
		if (!input.value) {
			return;
		}
		input.value = '';
		input.placeholder = '正在验证..';
		sendRequest(window.api.method,
			window.api.url,
			window.api.formatPayload(files.parent, passwd),
			window.api.headers,
			data => {
				const newFiles = JSON.parse(data);
				if (newFiles.encrypted) {
					input.placeholder = '密码错误';
				} else {
					window.fileCache.set(newFiles.parent, newFiles);
					fetchFileList(newFiles.parent);
				}
			},
			() => window.fileCache.set(newFiles.parent, false)
		);
	});
}

function addPathListener(elem, path) {
	elem.addEventListener('click', event => {
		fetchFileList(path);
		switchBackForwardStatus(path);
	});
}

function addTreeNodeListener(elem, path) {
	elem.addEventListener('click', event => {
		fetchFileList(path);
		switchBackForwardStatus(path);
	});
}

function addFileListLineListener(elem, path, url, size) {
	if (url) {
		elem.addEventListener('click', event => {
			window.backFordwardCache.preview = true;
			const previewHandler = {
				copyTextContent: (source, text) => {
					let result = false;
					let target = document.createElement('pre');
					target.style.opacity = '0';
					target.textContent = text || source.textContent;
					document.body.appendChild(target);
					try {
						let range = document.createRange();
						range.selectNode(target);
						window.getSelection().removeAllRanges();
						window.getSelection().addRange(range);
						document.execCommand('copy');
						window.getSelection().removeAllRanges();
						result = true;
					} catch (e) { }
					document.body.removeChild(target);
					return result;
				},
				fileType: suffix => {
					Array.prototype.contains = function (search) {
						const object = this;
						for (const key in object) {
							if (object.hasOwnProperty(key)) {
								if ((eval('/^' + search + '$/i')).test(object[key])) {
									return true;
								}
							}
						}
						return false;
					};
					if (['bmp', 'jpg', 'png', 'svg', 'webp', 'gif'].contains(suffix)) {
						return 'image';
					} else if (['mp3', 'flac', 'wav'].contains(suffix)) {
						return 'audio';
					} else if (['mp4', 'avi', 'mkv', 'flv', 'm3u8'].contains(suffix)) {
						return 'video';
					} else if (
						[
							'txt', 'js', 'json', 'css', 'html', 'java', 'c', 'cpp', 'php',
							'cmd', 'ps1', 'bat', 'sh', 'py', 'go', 'asp', 'reg'
						].contains(suffix)
					) {
						return 'text';
					} else if (
						['doc', 'docx', 'ppt', 'pptx', 'xls', 'xlsx', 'mpp', 'rtf', 'vsd', 'vsdx'].contains(suffix)
					) {
						return 'office';
					} else if (['pdf'].contains(suffix)) {
						return 'pdf';
					} else if (['md'].contains(suffix)) {
						return 'markdown';
					};
				},
				loadResource: (resource, callback) => {
					let type;
					switch (resource.split('.').pop()) {
						case 'css':
							type = 'link';
							break;
						case 'js':
							type = 'script';
							break;
					}
					let element = document.createElement(type);
					let loaded = false;
					if (typeof callback === 'function') {
						element.onload = element.onreadystatechange = () => {
							if (!loaded && (!element.readyState || /loaded|complete/.test(
								element.readyState))) {
								element.onload = element.onreadystatechange = null;
								loaded = true;
								callback();
							}
						}
					}
					if (type === 'link') {
						element.href = resource;
						element.rel = 'stylesheet';
					} else {
						element.src = resource;
					}
					document.getElementsByTagName('head')[0].appendChild(element);
				},

				createDplayer: (video, type, elem) => {
					const host = 'https://s0.pstatp.com/cdn/expire-1-M';
					const resources = [
						'/dplayer/1.25.0/DPlayer.min.css',
						'/dplayer/1.25.0/DPlayer.min.js',
						'/hls.js/0.12.4/hls.light.min.js',
						'/flv.js/1.5.0/flv.min.js'
					];
					let unloadedResourceCount = resources.length;
					resources.forEach(resource => {
						previewHandler.loadResource(host + resource, () => {
							if (!--unloadedResourceCount) {
								let option = {
									url: video
								}
								if (type === 'flv') {
									option.type = 'flv';
								}
								new DPlayer({
									container: elem,
									volume: 0.5,
									screenshot: true,
									video: option
								});
							}
						})
					});
				}
			}
			const suffix = path.split('.').pop();
			let content = document.querySelector('.content');
			let contentType = previewHandler.fileType(suffix);
			switch (contentType) {
				case 'image':
					let img = new Image();
					img.style.maxWidth = '100%';
					img.style.border = '4px dotted #94b4d1';
					img.style.padding = '6px';
					img.src = url;
					let fancy = document.createElement('a');
					fancy.setAttribute('data-fancybox', 'image');
					fancy.href = img.src;
					fancy.append(img);
					content.innerHTML = '';
					content.append(fancy);
					break;
				case 'audio':
					let audio = new Audio();
					audio.style.outline = 'none';
					audio.preload = 'auto';
					audio.volume = 0.3;
					audio.controls = 'controls';
					audio.style.width = '100%';
					audio.src = url;
					content.innerHTML = '';
					content.append(audio);
					break;
				case 'video':
					let video = document.createElement('div');
					previewHandler.createDplayer(url, suffix, video);
					content.innerHTML = '';
					content.append(video);

					/*
					var x = document.createElement("VIDEO");
					x.setAttribute("width", "100%");
					x.setAttribute("controls", "controls");
					alert(url);
					x.setAttribute("src", url);
					content.append(x);
					*/

					break;
				case 'text':
					let pre = document.createElement('pre');
					let code = document.createElement('code');
					pre.append(code);
					//pre.style.background = 'rgb(245,245,245)';
					pre.style['overflow-x'] = 'scroll';
					pre.classList.add(suffix);
					content.style['text-align'] = 'initial';
					content.innerHTML = '';
					content.append(pre);
					sendRequest('GET', url, null, null, data => {
						code.textContent = data;
						if (size.indexOf(' B') >= 0 || size.indexOf(' KB') &&
							size.split(' ')[0] < 100
						) {
							//hljs.highlightBlock(pre);
							$('pre code').each(function(i, block) {
								hljs.highlightBlock(block);
								hljs.lineNumbersBlock(block);
							});
						}
					});
					break;
				case 'office':
					const officeOnline = 'https://view.officeapps.live.com/op/view.aspx?src=' + encodeURIComponent(url);
					//window.open(officeOnline);
					let div = document.createElement('div');
					div.className = 'target-blank';
					div.innerHTML = '点我新窗口打开';
					div.addEventListener('click', () => window.open(officeOnline));
					content.innerHTML = '';
					content.appendChild(div);
					if (document.body.clientWidth >= 480) {
						let iframe = document.createElement('iframe');
						iframe.width = '100%';
						iframe.style.height = '41em';
						iframe.style.border = '0';
						iframe.src = officeOnline;
						content.appendChild(iframe);
					}
					break;
				case 'pdf':
					//const pdfOnline = '//web.jisupdf.com/?file=' + encodeURIComponent(url);
					//const pdfOnline = '//mozilla.github.io/pdf.js/web/viewer.html?file=' + encodeURIComponent(url);
					const pdfOnline = 'https://pdf.rawchen.com/web?file=' + encodeURIComponent(url);
					//window.open(pdfOnline);
					let div2 = document.createElement('div');
					div2.className = 'target-blank';
					div2.innerHTML = '点我新窗口打开';
					div2.addEventListener('click', () => window.open(pdfOnline));
					content.innerHTML = '';
					content.appendChild(div2);
					if (document.body.clientWidth >= 300) {
						let iframe2 = document.createElement('iframe');
						iframe2.width = '100%';
						iframe2.style.height = '41em';
						iframe2.style.border = '0';
						iframe2.src = pdfOnline;
						content.appendChild(iframe2);
					}
					break;
				case 'markdown':
					renderMarkdown(path, url);
					break;
				default:
					// content.style['color'] = 'white';
					// content.style['background-color'] = '#131313';
					// content.style['font-size'] = '20px';
					content.style['text-align'] = 'center';
					content.innerHTML = '该文件不支持预览';
					break;

			}
			path = window.GLOBAL_CONFIG.SCF_GATEWAY + '/?file=' + path;
			document.querySelector('.file-name').innerHTML = path;
			document.querySelector('.btn.download').addEventListener('click',
				() => {
					previewHandler.copyTextContent(null,url);
					location.href = url;
					const btn = document.querySelector('.btn.download');
					btn.innerHTML = '已复制';
					setTimeout(() => btn.innerHTML = '下载', 3000);
				}
			);
			document.querySelector('.btn.quote').addEventListener('click',
				event => {
					previewHandler.copyTextContent(null, path);
					const btn = document.querySelector('.btn.quote');
					btn.innerHTML = '已复制';
					setTimeout(() => btn.innerHTML = '引用', 3000);
				}
			);
			document.querySelector('.btn.share').addEventListener('click',
				event => {
					const sharePath = () => {
						let arr = window.backFordwardCache.current.split('/');
						let r = '';
						for (let i = 1; i < arr.length; i++) {
							r += '/' + arr[i];
						}
						return r;
					}
					previewHandler.copyTextContent(null,
						window.location.origin +
						window.location.pathname +
						'?path=' + sharePath());
					const btn = document.querySelector('.btn.share');
					btn.innerHTML = '已复制';
					setTimeout(() => btn.innerHTML = '分享', 3000);
				}
			);
			switchRightDisplay('preview');

			let start = null;
			let right = document.querySelector('.right');
			const scrollToBottom = (timestamp) => {
				if (!start) start = timestamp;
				let progress = timestamp - start;
				let last = right.scrollTop;
				right.scrollTo(0, right.scrollTop + 14);
				if (right.scrollTop !== last && progress < 1000 * 2) {
					window.requestAnimationFrame(scrollToBottom);
				}
			};
			window.requestAnimationFrame(scrollToBottom);
		});
	} else {
		elem.addEventListener('click', event => {
			fetchFileList(path);
			switchBackForwardStatus(path);
		});
	}
}

function addBackForwardListener() {
	document.getElementById('arrow-back').addEventListener('click', back);
	document.getElementById('arrow-forward').addEventListener('click', forward);
	document.querySelector('#main-page').addEventListener('click', () => {
		fetchFileList(window.api.root);
		switchBackForwardStatus(window.api.root);
	});
}

function switchRightDisplay(display) {
	if (display === 'preview') {
		document.querySelector('.list-header').style.display = 'none';
		document.querySelector('#file-list').style.display = 'none';
		document.querySelector('.markdown-body').style.display = 'none';
		document.querySelector('.password').style.display = 'none';
		document.querySelector('.preview').style.display = 'initial'
	} else if (display === 'encrypted') {
		document.querySelector('.list-header').style.display = 'none';
		document.querySelector('#file-list').style.display = 'none';
		document.querySelector('.markdown-body').style.display = 'none';
		document.querySelector('.preview').style.display = 'none';
		document.querySelector('.password').style.display = 'initial';
		document.querySelector('#readme').innerHTML = '';
		let content = document.querySelector('.preview .content');
		if (content) {
			document.querySelector('.preview .content').innerHTML = '';
		}
	} else {
		document.querySelector('.list-header').style.display = 'initial';
		document.querySelector('#file-list').style.display = 'initial';
		document.querySelector('.markdown-body').style.display = 'none'
		document.querySelector('.preview').style.display = 'none';
		document.querySelector('.password').style.display = 'none';
		document.querySelector('#readme').innerHTML = '';
		let content = document.querySelector('.preview .content');
		if (content) {
			document.querySelector('.preview .content').innerHTML = '';
		}
	}
}

function switchBackForwardStatus(path) {
	if (path) {
		window.backFordwardCache.deepest = path;
	}
	if (window.backFordwardCache.root !== window.backFordwardCache.current) {
		window.backFordwardCache.backable = true;
		document.getElementById('arrow-back').style.color = '#545454';
	} else {
		// window.backFordwardCache.backable = false;
		// document.getElementById('arrow-back').style.color = 'rgb(218, 215, 215)';
	}
	if (window.backFordwardCache.deepest !== window.backFordwardCache.current) {
		window.backFordwardCache.forwardable = true;
		document.getElementById('arrow-forward').style.color = '#545454';
	} else {
		window.backFordwardCache.forwardable = false;
		document.getElementById('arrow-forward').style.color = '#545454';
	}
}

function back() {
	if (!window.backFordwardCache.backable) {
		return;
	}
	if (window.backFordwardCache.preview) {
		fetchFileList(window.backFordwardCache.current);
	} else {
		let former = (() => {
			let formerEndIndex = window.backFordwardCache.current.lastIndexOf('/');
			return window.backFordwardCache.current.substring(0, formerEndIndex);
		})();
		former = former || window.api.root;
		fetchFileList(former);
		switchBackForwardStatus();
	}
	// console.log(window.backFordwardCache);
}

function forward() {
	if (!window.backFordwardCache.forwardable) {
		return
	}
	const current = window.backFordwardCache.current === window.api.root ? '' : window.backFordwardCache.current
	const subLength = current ? current.length : 0;
	const later = current + '/' +
		window.backFordwardCache.deepest.substring(subLength).split('/')[1];
	fetchFileList(later);
	switchBackForwardStatus();
	// console.log(window.backFordwardCache);
}

async function preCache(files, level) {
	if (level > 2) return;
	files.files.forEach(file => {
		const parent = files.parent === '/' ? '' : files.parent
		const path = parent + '/' + file.name;
		if (!file.url) {
			// console.log('caching ' + path + ', level ' + level);
			window.fileCache.set(path, true);
			sendRequest(window.api.method,
				window.api.url,
				window.api.formatPayload(path),
				window.api.headers,
				data => {
					const files = JSON.parse(data);
					window.fileCache.set(path, files);
					preCache(files, level + 1);
				},
				() => window.fileCache.set(path, false)
			);
		} else if (file.name.split('.').pop() === 'md') {
			// console.log('caching ' + path + ', level ' + level);
			window.fileCache.set(path, true);
			sendRequest('GET', file.url, null, null, text => window.fileCache.set(path, text), () => window.fileCache.set(path, false));
		}
	});
}

async function preCacheCheck(cache, path) {
	cache.files.forEach(file => {
		const prefix = path === window.api.root ? '' : path;
		const nextPath = prefix + '/' + file.name;
		const pathCache = window.fileCache.get(nextPath);
		if (!file.url) {
			if (!pathCache && pathCache !== true) {
				// console.log('inner caching ' + nextPath);
				window.fileCache.set(nextPath, true);
				sendRequest(window.api.method,
					window.api.url,
					window.api.formatPayload(nextPath),
					window.api.headers,
					data => {
						const files = JSON.parse(data);
						window.fileCache.set(nextPath, files);
						preCache(files, 0);
					},
					() => window.fileCache.set(nextPath, false)
				);
			}
		} else if (file.name.split('.').pop() === 'md') {
			if (!pathCache && pathCache !== true) {
				// console.log('inner caching ' + nextPath);
				window.fileCache.set(nextPath, true);
				sendRequest('GET', file.url, null, null, text => window.fileCache.set(nextPath,
					text), () => window.fileCache.set(nextPath,
					false));
			}
		}
	});
}

function fetchFileList(path) {
	// console.log('fetching ' + path);
	let loading = document.querySelector('.loading-wrapper');
	loading.style.display = 'initial';
	window.backFordwardCache.preview = false;
	window.backFordwardCache.current = path;
	let cache = window.fileCache.get(path);
	if (cache === true) {
		let cacheWaitFileListFetch = setInterval(() => {
			cache = window.fileCache.get(path);
			if (typeof cache === 'object') {
				renderPage(null, cache);
				preCacheCheck(cache, path);
				clearInterval(cacheWaitFileListFetch);
			} else if (cache === false) {
				clearInterval(cacheWaitFileListFetch);
				loading.style.color = 'red';
				loading.innerText = 'Failed!';
				setTimeout(() => {
					loading.style.display = 'none';
					loading.style.color = 'white';
					loading.innerText = 'Loading..';
				}, 2000);
			}
		}, 100);
	} else if (cache) {
		renderPage(null, cache);
		preCacheCheck(cache, path);
	} else {
		//第一次经过此
		window.fileCache.set(path, true);
		//path = '/'
		sendRequest(window.api.method,
			// window.api.url,
			"http://localhost:8899/folder/root",
			window.api.formatPayload(path),
			window.api.headers,
			renderPage
		);
	}
}