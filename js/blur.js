	function unblur() {
		this.blur();
	}
	function getLinksToBlur() {
		if (!document.getElementById) return
		links = document.getElementsByTagName("a");
		for(i=0; i<links.length; i++) {
			links[i].onfocus = unblur
		}
	}

	function getLinksToBlur2() {
		if (!document.getElementById) return
		links = document.getElementsByTagName("area");
		for(i=0; i<links.length; i++) {
			links[i].onfocus = unblur
		}
	}