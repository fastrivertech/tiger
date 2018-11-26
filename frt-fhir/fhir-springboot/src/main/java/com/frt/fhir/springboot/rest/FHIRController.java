package com.frt.fhir.springboot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.frt.fhir.rest.ResourcePath;
import com.frt.fhir.springboot.rest.FHIRResourceService;
import com.frt.util.logging.Localization;
import com.frt.util.logging.Logger;

@RestController
@RequestMapping("/frt-fhir-rest"+ResourcePath.BASE_PATH)
class FHIRController {
	private static Logger logger = Logger.getLog(FHIRController.class.getName());
	private static Localization localizer = Localization.getInstance();

	@Autowired
	private FHIRResourceService fResourceService;

	@GetMapping(value=ResourcePath.TYPE_PATH + ResourcePath.ID_PATH, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String readResource(@PathVariable final String type, 
			@PathVariable final String id,
			@RequestParam(value = "_format", defaultValue = "json") final String _format,
			@RequestParam(value = "_summary", defaultValue = "false") String _summary) {
		logger.info(localizer.x("FHIRController.read(...), id=" + id + ", type=" + type));		
		return fResourceService.read(type, id, _format, _summary);
	}

	@PostMapping(ResourcePath.TYPE_PATH)
	public ResponseEntity<String> createResource(@PathVariable final String type, 
			@RequestParam(value = "_format", defaultValue = "json") final String _format, 
			@RequestBody final String body) {
		logger.info(localizer.x("FHIRController.read(...), type=" + type + ", body=" + body));		
		return fResourceService.create(type, _format, body);
	}
}