package dpp.reporting;

public class AppHelp {
	// @formatter:off
	static final String[] HELP_TEXT = {
			"DPP Application, converting compatible MXF files to UK DPP MXF files. Usage: -",
			"    java -jar dpp-0.0.1-SNAPSHOT-jar-with-dependencies.jar -c<COMMAND> ",
			"    [-f<mxf.file>] ",
			"    [-x<xml.file>] ",
			"    [-t<transwrapped-mxf.file>] ",
			"    [-s<sidecar.xml.file>] ",
			"    [-r<status-report.file>] ",
			"    [-b<path to binaries>] (optional, assumes binaries in path if not specified)",
			"    [-o] (overwrite files)",
			"    [-d] (debug mode, don't clean up temp files)",
			" ",
			"where COMMAND is one of: -",
			"    EXTRACT (extract metadata from MXF file and populate XML document with it)",
			"    GENERATE_MXF_AND_XML (generate DPP compliant MXF file and XML sidecar from conformant MXF file and input XML file)",
			"    GENERATE_SIDECAR (generate XML sidecar from DPP MXF file)",
			"    VALIDATE_XML (validate XML file against DPP Schema)",
			"    VALIDATE_DPP (validate XML and MXF files against DPP specification)",
			"    VALIDATE_EDITORIAL (validate only editorarial data)",
			"    VALIDATE_VIDEO (validate only video data)",
			"    VALIDATE_TIMECODE (validate only timecode data)",
			"    VALIDATE_OTHERS (validate only additional data not validated elsewhere)",
			"    VALIDATE_AUDIO (validate only audio data)",
			" ",
			"    e.g. java -jar dpp.jar -cEXTRACT -fmy-mxf.mxf -xmy-xml.xml -rreport.txt ",
			"         extracts metadata from my-mxf.mxf file and populates my-xml.xml file, ",
			"         validation errors will be reported in report.txt",
			" ",
			"    e.g. java -jar dpp.jar -cgenerate_mxf_and_xml -fmy-mxf.mxf -xmy-xml.xml -tmy-dpp.mxf -smy-sidecar.xml -r report.txt ",
			"         performs validation on my-mxf.mxf and my-xml.xml ",
			"         and if valid generates dpp mxf file my-dpp.mxf and my-sidecar.xml file",
			" ",
			"When transwrapping, the command will abort if the transwrapped file already exists unless the overwrite flag -o is specified",
			" ",
			"The status report file will contain the mxf file type (OP1A/AS11/DPP) where this can be determined ",
			"and any errors encountered during processing. ",
			"If the -r option is not specified then it is written to standard out." };
	// @formatter:on

	public static void displayHelp() {

		for (String string : HELP_TEXT) {
			System.out.println(string);
		}
	}

}
