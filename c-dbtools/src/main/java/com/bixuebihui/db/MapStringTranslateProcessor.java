
    /**
     * Map of String translate, to export csv, translate k-v pair
     */
    public class MapStringTranslateProcessor extends CellProcessorAdaptor {

        private final Map<String, String> map;

        MapStringTranslateProcessor(Map<String,String> map){
            this.map = map;
        }


        @Override
        public Object execute(Object value, final CsvContext context) {
            if (value == null) {
                return "";
            }
            return map.getOrDefault(value+"", value+"");
        }

    }
