package org.ligi.blexplorer.util;

import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import java.math.BigInteger;

public final class ManufacturerRecordParserFactory {

    private static final String TAG = "ManufacturerParser";

    private final SparseArray<Class<? extends ManufacturerParserBase>> mParserTemplates =
            new SparseArray<Class<? extends ManufacturerParserBase>>();

    static protected ManufacturerRecordParserFactory sParserFactory;

    static {
        // Load the factory with the samplers we support
        sParserFactory = new ManufacturerRecordParserFactory(
                IBeaconParser.class
        );
    }

    static public ManufacturerParserBase parse(int cic, byte[] record) {
        ManufacturerParserBase p = sParserFactory.getParser(cic);
        if (p != null)
            if (!p.parse(record))
                return null;
        return p;
    }

    // Convenience constructor, variable arguments
    public ManufacturerRecordParserFactory(Class<? extends ManufacturerParserBase>... parserClasses) {
        for (Class<? extends ManufacturerParserBase> parser : parserClasses) {
            putParser(parser);
        }
    }

    public final void putParser(Class<? extends ManufacturerParserBase> Parser) {
        // Instantiate a temporary of the class so that we can query some
        // of the virtuals
        ManufacturerParserBase parserObject;
        try {
            parserObject = Parser.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        int companyIdentifierCode = parserObject.getCompanyIdentifierCode();
        if (mParserTemplates.get(companyIdentifierCode) != null) {
            Log.e(TAG, "Can't add the same (" + companyIdentifierCode + ") parser twice");
            return;
        }
        mParserTemplates.put(companyIdentifierCode, Parser);
    }

    @Nullable
    public ManufacturerParserBase getParser(int companyIdentifierCode) {
        Class<? extends ManufacturerParserBase> parserClass = mParserTemplates.get(companyIdentifierCode);
        if (parserClass == null) {
            return null;
        }
        try {
            ManufacturerParserBase parser = parserClass.newInstance();
            if (parser == null)
                return null;
            return parser;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }


    static public abstract class ManufacturerParserBase {
        protected int mType;
        protected int mLen;
        protected byte[] mBytes = null;

        public ManufacturerParserBase() {
        }

        /**
         * https://www.bluetooth.org/en-us/specification/assigned-numbers/company-identifiers
         *
         * @return code from
         */
        public abstract int getCompanyIdentifierCode();

        public String getKeyDescriptor() {
            return "" + getCompanyIdentifierCode();
        }

        /**
         * Expect to be given that part of the scanRecord that was the 0xff manufacturer specifc
         * record, but with the manufacturer identifier already removed, and the array clipped
         * to the size of the field.
         *
         * @return success
         */
        public abstract boolean parse(byte[] manufacturerData);
    }

    static protected class IBeaconParser extends ManufacturerParserBase {
        private BigInteger mUUID;
        private int mMajor;
        private int mMinor;
        private int mTXPower;

        public IBeaconParser() {
            super();
        }

        @Override
        public int getCompanyIdentifierCode() {
            return 0x4c;
        }

        public String getKeyDescriptor() {
            return "iBeacon";
        }

        @Override
        public boolean parse(byte[] manufacturerData) {
            // <apple record type> <apple record len> <apple record>
            int index = 0;
            while (index < manufacturerData.length) {
                mType = manufacturerData[index] & 0xff;
                mLen = manufacturerData[index + 1] & 0xff;

                mBytes = new byte[mLen];
                System.arraycopy(manufacturerData, index + 2, mBytes, 0, mBytes.length);
                index += mLen + 2;

                // Only support iBeacon parsing
                if (mType != 0x02) {
                    continue;
                }

                byte[] uuidbytes = new byte[16];
                System.arraycopy(mBytes, 0, uuidbytes, 0, uuidbytes.length);
                mUUID = new BigInteger(1, uuidbytes);
                mMajor = (mBytes[17] << 8 | mBytes[16]) & 0xffff;
                mMinor = (mBytes[19] << 8 | mBytes[18]) & 0xffff;
                mTXPower = mBytes[20];
            }

            return false;
        }

        @Override
        public String toString() {
            return "uuid = " + mUUID.toString(16)
                    + "\nmajor = " + mMajor + "\nminor = " + mMinor
                    + "\ntxPower = " + mTXPower + "dBm";
        }
    }
}

