/**
 * 
 */
package com.gae.jersey.test;

import java.util.Comparator;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Derek Altamirano
 *
 */
public class FileData extends FileInfo {

	private byte[] data;

	public static final Comparator<FileData> CASE_INSENSITIVE_ORDER = new FileDataComparator();

	private static class FileDataComparator implements Comparator<FileData> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(FileData o1, FileData o2) {
			final String s1 = (null == o1 ? null : o1.getFileName());
			final String s2 = (null == o2 ? null : o2.getFileName());

			return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
		}

	}

	/**
	 * @param fileName
	 */
	public FileData(String fileName) {
		this(fileName, null);
	}

	public FileData(String fileName, final byte[] data) {
		super(fileName, (null == data ? 0 : data.length));

		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.disneyinteractive.devon.core.FileInfo#getLength()
	 */
	@Override
	public long getLength() {
		return (null == data ? 0 : data.length);
	}

	/**
	 * Gets the file data
	 * 
	 * @return the data
	 */
	public byte[] getData() {
		return (null == data ? ArrayUtils.EMPTY_BYTE_ARRAY : data);
	}

	/**
	 * Sets the file data
	 * 
	 * @param data
	 *            the data to set
	 */
	public void setData(final byte[] data) {
		this.data = data;
	}

}
