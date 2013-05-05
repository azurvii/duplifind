package duplifind

final String VERSION = "0.0.2"

println "Duplifinder ${VERSION}"
if (args.length < 1) {
	println "Please tell me which folder you want to exam."
} else {
	println "Looking into ${args[0]}"
	Duplifinder dfinder = new Duplifinder()
	Utility.setOutfile(File.createTempFile("duplifinder", "log"))
	dfinder.setDigestFile(File.createTempFile("duplifinder", "digest"))
	Utility.clearLog()
	dfinder.reset()
	dfinder.process(new File(args[0]))
//	dfinder.dumpDigests()
//	dfinder.reportDuplicates()
	dfinder.clearDuplicates()
}
