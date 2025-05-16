package com.weiyin.qinplus.ui.tv.bwstaff.DB;
    public class TagEvent {
        public  short trkNum;
        public short getTrkNum() {
			return trkNum;
		}
		public void setTrkNum(short trkNum) {
			this.trkNum = trkNum;
		}
		public int getAbsTicks() {
			return absTicks;
		}
		public void setAbsTicks(int absTicks) {
			this.absTicks = absTicks;
		}
		public int getTimeStamp() {
			return timeStamp;
		}
		public void setTimeStamp(int timeStamp) {
			this.timeStamp = timeStamp;
		}
		public char getChn() {
			return Chn;
		}
		public void setChn(char chn) {
			Chn = chn;
		}
		public int getLen() {
			return len;
		}
		public void setLen(int len) {
			this.len = len;
		}
		public int[] getData() {
			return data;
		}
		public void setData(int[] data) {
			this.data = data;
		}
		public int absTicks;

        public int timeStamp;
//        tagEventType type;

        public char Chn;
//        tagMetaType metaType;

        public  int len;
        public int[] data;
    }