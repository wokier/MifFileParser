package org.philhosoft.mif.parser;

import java.util.ArrayList;
import java.util.List;

import org.philhosoft.mif.model.MifFileContent;
import org.philhosoft.mif.model.data.MifData;
import org.philhosoft.mif.parser.data.MifArcParser;
import org.philhosoft.mif.parser.data.MifDataParser;
import org.philhosoft.mif.parser.data.MifEllipseParser;
import org.philhosoft.mif.parser.data.MifLineParser;
import org.philhosoft.mif.parser.data.MifPointParser;
import org.philhosoft.mif.parser.data.MifPolylineParser;
import org.philhosoft.mif.parser.data.MifRectangleParser;
import org.philhosoft.mif.parser.data.MifRegionParser;
import org.philhosoft.mif.parser.data.MifRoundedRectangleParser;
import org.philhosoft.mif.parser.data.MifTextParser;

public class MifFileContentParser
{
	private List<MifDataParser> parsers = new ArrayList<>();

	public MifFileContentParser()
	{
		parsers.add(new MifRegionParser());
		parsers.add(new MifPolylineParser());
		parsers.add(new MifPointParser());
		parsers.add(new MifTextParser());
		parsers.add(new MifLineParser());
		parsers.add(new MifRectangleParser());
		parsers.add(new MifRoundedRectangleParser());
		parsers.add(new MifPointParser());
		parsers.add(new MifEllipseParser());
		parsers.add(new MifArcParser());
	}

	public MifFileContent parseContent(MifReader reader)
	{
		MifHeaderParser headerParser = new MifHeaderParser();
		MifFileContent fileContent = headerParser.parse(reader);
		if (reader.getMessageCollector().hasErrors())
		{
			return fileContent; // Partial content?
		}

		while (reader.readNextLine())
		{
			boolean parsed = false;
			for (MifDataParser parser : parsers)
			{
				if (parser.canParse(reader))
				{
					MifData data = parser.parseData(reader);
					fileContent.add(data);
					parsed = true;
					break;
				}
			}
			if (!parsed)
			{
				reader.addWarning("Unrecognized line " + reader.getCurrentLineNumber() + ";ignored");
			}
		}

		return fileContent;
	}
}