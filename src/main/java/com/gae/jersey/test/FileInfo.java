/**
 * 
 */
package com.gae.jersey.test;

import org.apache.commons.lang3.StringUtils;

/**
 * Virtual File information when importing or exporting a file.
 * 
 * @author Derek Altamirano
 *
 */
public class FileInfo {

	private final String fileName;

	private final long length;

	/**
	 * Constructs a new {@code FileInfo} object.
	 * 
	 * @param fileName
	 *            The file name
	 * @param length
	 *            The file length
	 */
	public FileInfo(final String fileName, final long length) {
		this.fileName = fileName;
		this.length = length;
	}

	/**
	 * The file name. Can be just the file name, or can contain a relative virtual path.
	 * 
	 * @return The file name.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Gets the file size
	 * 
	 * @return The size in bytes
	 */
	public long getLength() {
		return length;
	}

	/**
	 * Gets the base file name (without the extension)
	 * 
	 * @return the base file name
	 */
	public String baseFileName() {
		final String baseNameWithExt = fileNameWithExtension();
		final int idx = StringUtils.lastIndexOf(baseNameWithExt, ".");
		if (idx != StringUtils.INDEX_NOT_FOUND) {
			final String baseName = StringUtils.substring(baseNameWithExt, 0, idx);
			return baseName;
		} else {
			return baseNameWithExt;
		}
	}

	/**
	 * Gets the base file name (without the extension)
	 * 
	 * @return the base file name
	 */
	public String fileNameWithExtension() {
		final int idx = StringUtils.lastIndexOfAny(fileName, "/", "\\");
		if (idx != StringUtils.INDEX_NOT_FOUND) {
			final String baseNameWithExt = StringUtils.substring(fileName, idx + 1);
			return baseNameWithExt;
		} else {
			return fileName;
		}
	}

	/**
	 * Gets the extension, including the '.' separator; never returns {@code null}. A file with no extension will return
	 * "".
	 * 
	 * @return the file extension
	 */
	public String extension() {
		final int idx = StringUtils.lastIndexOf(fileName, ".");
		if (idx != StringUtils.INDEX_NOT_FOUND) {
			final String ext = StringUtils.substring(fileName, idx);
			return ext;
		} else {
			return "";
		}
	}

}
