package dpp.xmlmarshalling;

import static dpp.ObjectFactoryConstants.DPP_OBJECT_FACTORY;
import dpp.schema.AccessServices;
import dpp.schema.Additional;
import dpp.schema.Audio;
import dpp.schema.ContactInformation;
import dpp.schema.Editorial;
import dpp.schema.Programme;
import dpp.schema.SigningEnum;
import dpp.schema.Technical;
import dpp.schema.Timecodes;
import dpp.schema.Video;

/**
 * Creates an empty Programme with required sections created but without data.
 * 
 */
public class ProgrammeBuilder {

	public static Programme createEmptyProgramme() {
		final Programme programme = DPP_OBJECT_FACTORY.createProgramme();

		Technical technical = DPP_OBJECT_FACTORY.createTechnical();
		Editorial editorial = DPP_OBJECT_FACTORY.createEditorial();

		programme.setEditorial(editorial);
		programme.setTechnical(technical);

		AccessServices accessServices = DPP_OBJECT_FACTORY.createAccessServices();
		populateAccessServices(accessServices);
		technical.setAccessServices(accessServices);

		Additional additional = DPP_OBJECT_FACTORY.createAdditional();
		populateAdditional(additional);
		technical.setAdditional(additional);

		Audio audio = DPP_OBJECT_FACTORY.createAudio();
		technical.setAudio(audio);
		audio.setAudioSamplingFrequency(DPP_OBJECT_FACTORY.createAudiosamplingfrequencyDt());

		ContactInformation contactInformation = DPP_OBJECT_FACTORY.createContactInformation();
		technical.setContactInformation(contactInformation);

		Timecodes timecodes = DPP_OBJECT_FACTORY.createTimecodes();
		timecodes.setParts(DPP_OBJECT_FACTORY.createParts());
		technical.setTimecodes(timecodes);

		Video video = DPP_OBJECT_FACTORY.createVideo();
		technical.setVideo(video);
		video.setVideoBitRate(DPP_OBJECT_FACTORY.createVideobitrateDt());

		return programme;
	}

	private static void populateAdditional(final Additional additional) {
		additional.setProgrammeHasText(false);
		additional.setTextlessElementExist(false);

	}

	private static void populateAccessServices(final AccessServices accessServices) {
		accessServices.setAudioDescriptionPresent(false);
		accessServices.setClosedCaptionsPresent(false);
		accessServices.setOpenCaptionsPresent(false);
		accessServices.setSigningPresent(SigningEnum.NO);

	}
}
