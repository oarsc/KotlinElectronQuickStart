const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld(
	'tools', {
		getMode: () => {
			const arg = process.argv.find(arg => arg.startsWith('--mode='));
			return arg ? arg.split('=')[1] : null;
		}
	}
);

contextBridge.exposeInMainWorld(
	'ipcRenderer', {
		send: (channel, ...data) => {
			ipcRenderer.send(channel, ...data);
		},
		sendSync: (channel, ...data) => {
			return ipcRenderer.sendSync(channel, ...data);
		},
		receive: (channel, func) => {
			ipcRenderer.on(channel, (event, ...args) => func(...args));
		}
	}
);
