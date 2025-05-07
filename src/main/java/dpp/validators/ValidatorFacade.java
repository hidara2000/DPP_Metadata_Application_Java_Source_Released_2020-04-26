package dpp.validators;

import org.apache.log4j.Logger;

import dpp.App;
import dpp.reporting.StatusReport;
import dpp.schema.Programme;

/**
 * Groups together the validation of individual aspects of validation, a sort of facade.
 */
public class ValidatorFacade {

	private static final Logger logger = Logger.getLogger(App.class);

	private final AccessServicesValidator accessServicesValidator = new AccessServicesValidator();
	private final AdditionalValidator additionalValidator = new AdditionalValidator();
	private final AudioValidator audioValidator = new AudioValidator();
	private final EditorialValidator editorialValidator = new EditorialValidator();
	private final TimecodeValidator timecodeValidator = new TimecodeValidator();
	private final VideoValidator videoValidator = new VideoValidator();
	private final StructuralValidator structuralValidator = new StructuralValidator();
	private final ShimNameValidator shimNameValidator = new ShimNameValidator();
	private final SchemaValidator validatorAgainstSchema = new SchemaValidator();

	public boolean validateFull(String xmlFilePath, Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		boolean validateAgainstSchema = validateAgainstSchema(xmlFilePath, statusReport);
		boolean validateProgrammeAgainstDppSpecification = validateProgrammeAgainstDppSpecification(programme, nonProgrammeData, statusReport);
		boolean fullValidationPassed = validateAgainstSchema && validateProgrammeAgainstDppSpecification;
		return fullValidationPassed;
	}

	public boolean validateProgrammeAgainstDppSpecification(Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		boolean validDppFile = false;

		boolean validAccessServices = accessServicesValidator.validateAccessServices(programme, statusReport);
		boolean validAdditional = additionalValidator.validateAdditional(programme, statusReport);
		boolean validAudio = audioValidator.validateAudio(programme, nonProgrammeData, statusReport, false);
		boolean validEditorial = editorialValidator.validateEditorial(programme, statusReport);
		boolean validTimecode = timecodeValidator.validateTimecode(programme, nonProgrammeData, statusReport);
		boolean validVideo = videoValidator.validateVideo(programme, nonProgrammeData, statusReport, false);

		validDppFile = validAccessServices && validAdditional && validAudio && validEditorial && validTimecode && validVideo;

		return validDppFile;
	}

	public boolean validateStructuralMetadata(Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		boolean validVideo = videoValidator.validateVideo(programme, nonProgrammeData, statusReport, true);
		boolean validAudio = audioValidator.validateAudio(programme, nonProgrammeData, statusReport, true);
		boolean validStructure = structuralValidator.validate(programme, nonProgrammeData, statusReport);

		return validVideo && validAudio && validStructure;
	}

	public boolean validateAgainstSchema(String xmlFilePath, StatusReport statusReport) {
		return validatorAgainstSchema.validateXmlFileAgainstSchema(xmlFilePath, statusReport);
	}

	public boolean validateOthers(Programme programme, StatusReport statusReport) {
		boolean validAccessServices = accessServicesValidator.validateAccessServices(programme, statusReport);
		boolean validAdditional = additionalValidator.validateAdditional(programme, statusReport);
		return validAccessServices && validAdditional;
	}

	public boolean validateTimecode(Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		return timecodeValidator.validateTimecode(programme, nonProgrammeData, statusReport);
	}

	public boolean validateVideo(Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		return videoValidator.validateVideo(programme, nonProgrammeData, statusReport, false);
	}

	public boolean validateEditorial(Programme programme, StatusReport statusReport) {
		return editorialValidator.validateEditorial(programme, statusReport);
	}

	public boolean validateAudio(Programme programme, NonProgrammeData nonProgrammeData, StatusReport statusReport) {
		return audioValidator.validateAudio(programme, nonProgrammeData, statusReport, false);
	}

	public boolean validateShimName(Programme programme, NonProgrammeData nonProgrammeData, String shimVersion, StatusReport statusReport) {

		String shimName = programme.getTechnical().getShimName();
		boolean hd = nonProgrammeData.isHighDefinition();
		boolean valid = false;

		if (shimName != null) {
			valid = shimNameValidator.validateShimName(shimName, shimVersion, hd, statusReport);
		} else {
			logger.warn("No shim name found in technical metadata so unable to validate");
		}

		return valid;
	}
}
