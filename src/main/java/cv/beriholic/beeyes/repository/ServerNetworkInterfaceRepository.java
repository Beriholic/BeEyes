package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.dto.MachineIPAddressDTO;
import cv.beriholic.beeyes.models.entity.ServerNetworkInterfacesDO;
import cv.beriholic.beeyes.models.entity.ServerNetworkInterfacesDOTable;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.tuple.Tuple2;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
public class ServerNetworkInterfaceRepository extends BaseRepository<ServerNetworkInterfacesDO, ServerNetworkInterfacesDOTable, Long> {
    public ServerNetworkInterfaceRepository(JSqlClient sql) {
        super(sql, ServerNetworkInterfacesDOTable.$);
    }

    public MachineIPAddressDTO queryMachineIPAddress(Long serverId) {
        List<Tuple2<String[], String[]>> record = createQuery()
                .where(table.serverId().eq(serverId))
                .select(table.ipv4Address(), table.ipv6Address())
                .execute();

        String[] ipv4 = record.stream().map(Tuple2::get_1)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
        String[] ipv6 = record.stream().map(Tuple2::get_2)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);

        return new MachineIPAddressDTO(
                ipv4,
                ipv6
        );
    }
}
