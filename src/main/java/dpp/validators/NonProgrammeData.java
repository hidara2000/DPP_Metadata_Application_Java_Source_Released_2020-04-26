package dpp.validators;

import dpp.enums.MxfTypeEnum;
import dpp.util.Timecode;

/**
 * This class is used to store any transient data which we may obtain from the bmx library but which is not directly persisted in the Programme xml wrapper
 * generally this data is used for validation purposes.
 * 
 */
public class NonProgrammeData {

	@Override
	public String toString() {
		return "NonProgrammeData [mxfType=" + mxfType + ", totalNumberOfAudioChannels=" + totalNumberOfAudioChannels + ", duration=" + duration + ", editRate="
				+ editRate + ", highDefinition=" + highDefinition + ", videoEssenceType=" + videoEssenceType + "]";
	}

	private MxfTypeEnum mxfType = MxfTypeEnum.UNKNOWN;

	private Integer totalNumberOfAudioChannels = 0;
	private Long duration = 0L;
	private String editRate = "";
	private boolean highDefinition;
	private String videoEssenceType = "";
	private String materialStartTimecode;

	public Integer getTotalNumberOfAudioChannels() {
		return totalNumberOfAudioChannels;
	}

	public void setTotalNumberOfAudioChannels(Integer numberOfTracks) {
		this.totalNumberOfAudioChannels = numberOfTracks;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getEditRate() {
		return editRate;
	}

	public void setEditRate(String editRate) {
		this.editRate = editRate;
	}

	public void setHighDefinition(boolean highDefinition) {
		this.highDefinition = highDefinition;

	}

	public boolean isHighDefinition() {
		return highDefinition;
	}

	public MxfTypeEnum getMxfType() {
		return mxfType;
	}

	public void setMxfType(MxfTypeEnum mxfType) {
		this.mxfType = mxfType;
	}

	public String getVideoEssenceType() {
		return videoEssenceType;
	}

	public void setVideoEssenceType(String videoEssenceType) {
		this.videoEssenceType = videoEssenceType;
	}

	public String getMaterialStartTimecode() {
		return materialStartTimecode;
	}

	public Timecode getMaterialStartAsTimecode() {

		return Timecode.getInstance(materialStartTimecode);
	}

	public void setMaterialStartTimecode(String materialStartTimecode) {
		this.materialStartTimecode = materialStartTimecode;
	}

}
