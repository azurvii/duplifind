package duplifind

class Duplifinder {
	Map<String, Set<String>> digests = [:]
	int progressTotal = 1
	int progressCurrent = 0
	int progressDepth = 0
	File digestFile

	void reset() {
		progressCurrent = 0
		progressTotal = 1
		progressDepth = 0
		digests = [:]
		digestFile?.write("")
	}

	void process(File f) {
		++progressCurrent
		if (f?.isFile()){
			if (f.size() == 0) {
				Utility.log("${progressCurrent}/${progressTotal}\t* Empty file ${f.getCanonicalPath()}")
			} else {
				String hex = Digester.getMD5(f)
				if (digests[hex] == null) {
					digests[hex] = [f.getCanonicalPath()] as Set
				} else {
					digests[hex].add(f.getCanonicalPath())
				}
				Utility.log("${progressCurrent}/${progressTotal}\t${hex} ${f.getCanonicalPath()}")
				digestFile?.append("${hex} ${f.getCanonicalPath()}\n")
			}
		} else if (f?.isDirectory()) {
			Utility.log("${progressCurrent}/${progressTotal}\t* Directory ${f.getCanonicalPath()}")
			File[] files = f.listFiles()
			progressTotal += files.length
			++progressDepth
			files.each { process(it) }
			--progressDepth
		} else {
			Utility.log("${progressCurrent}/${progressTotal}\t* Invalid file ${f.getCanonicalPath()}")
		}
		if (progressDepth == 0 && progressCurrent == progressTotal) {
			Utility.log("* Scanned ${progressTotal} item(s)")
		}
	}

	void dumpDigests() {
		digestFile?.write("")
		digests.each { digest, files ->
			files.each {
				digestFile?.append("${digest} ${it}\n")
			}
		}
	}

	void reportDuplicates() {
		int duplicates = 0
		digests.each { digest, files ->
			if (files.size() > 1) {
				++duplicates
				Utility.log("* Duplication ${duplicates}")
				files.each { Utility.log(it) }
			}
		}
		if (duplicates == 0) {
			Utility.log("* No duplication found")
		} else {
			Utility.log("* Found ${duplicates} duplicate(s)")
		}
	}

	void clearDuplicates() {
		int duplicates = 0
		BufferedReader cin = System.in.newReader()
		digests.each { digest, files ->
			if (files.size() > 1) {
				++duplicates
				Utility.log("* Duplication ${duplicates}")
				files.eachWithIndex { it, i ->
					Utility.log("${i} ${it}")
				}
				print "Which to keep? (0-${files.size() - 1}, or 'a' to keep them all) [0]: "
				int indexToKeep
				String ans = cin.readLine()
				if (ans.isEmpty()) {
					indexToKeep = 0
				} else if (ans.equalsIgnoreCase("a")) {
					Utility.log("* Keep all duplicates")
					indexToKeep = -1
				} else {
					while (!ans.isNumber() || ans.toInteger() >= files.size()) {
						print "Sorry, that's impossible, try again (0-${files.size() - 1}, or 'a' to keep them all) [0]: "
						ans = cin.readLine()
					}
					indexToKeep = ans.toInteger()
				}
				if (indexToKeep != -1) {
					String fileToKeep = files[indexToKeep]
					Utility.log("* Keep ${fileToKeep}")
					files.each {
						if(!it.equals(fileToKeep)) {
							Utility.log("* Deleting ${it}")
							new File(it).delete()
						}
					}
					digests[digest] = [fileToKeep] as Set
				}
			}
		}
		if (duplicates == 0) {
			Utility.log("* No duplication found")
		} else {
			Utility.log("* Processed ${duplicates} duplication(s)")
		}
		dumpDigests()
	}
}
