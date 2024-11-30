import 'package:honnywellintermecpr3/honnywellintermecpr3.dart';

class HoneywellHelper {
  final _honeywell = Honnywellintermecpr3();

  Future<void> initiatePrint(
    String deviceName,
    String deviceBleutoothMacAdress,
    List<dynamic> cmd,
  ) async =>
      _honeywell.printGeneral(deviceName, deviceBleutoothMacAdress, cmd);
}
