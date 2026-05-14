package com.logistics.cargo_api.service;

import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.entity.enums.CargoStatus;
import com.logistics.cargo_api.entity.enums.CargoType;
import com.logistics.cargo_api.exception.EntityNotFoundException;
import com.logistics.cargo_api.factory.DangerousCargoFactory;
import com.logistics.cargo_api.factory.FragileCargoFactory;
import com.logistics.cargo_api.factory.RefrigeratedCargoFactory;
import com.logistics.cargo_api.factory.StandardCargoFactory;
import com.logistics.cargo_api.repository.CargoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("CargoService Tests")
class CargoServiceTest {

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private StandardCargoFactory standardCargoFactory;

    @Mock
    private FragileCargoFactory fragileCargoFactory;

    @Mock
    private DangerousCargoFactory dangerousCargoFactory;

    @Mock
    private RefrigeratedCargoFactory refrigeratedCargoFactory;

    @InjectMocks
    private CargoService cargoService;


    private Cargo buildCargo(Long id, String name, CargoType type, CargoStatus status) {
        return Cargo.builder()
                .id(id).name(name)
                .weightKg(100).volumeM3(2.0)
                .cargoType(type).status(status)
                .build();
    }


    @Test
    @DisplayName("✅ addCargo — позитивний сценарій: стандартний вантаж зберігається")
    void addCargo_validData_returnsSavedCargo() {
        Cargo saved = buildCargo(1L, "Меблі", CargoType.STANDARD, CargoStatus.ON_WAREHOUSE);
        when(standardCargoFactory.createCargo(anyString(), anyDouble(), anyDouble())).thenReturn(saved);
        when(cargoRepository.save(any(Cargo.class))).thenReturn(saved);

        Cargo result = cargoService.addCargo("Меблі", 100.0, 2.0, CargoType.STANDARD);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Меблі");
        assertThat(result.getStatus()).isEqualTo(CargoStatus.ON_WAREHOUSE);
        verify(cargoRepository, times(1)).save(any(Cargo.class));
    }

    @Test
    @DisplayName("✅ addCargo — позитивний сценарій: небезпечний вантаж")
    void addCargo_dangerousCargo_savedSuccessfully() {
        Cargo saved = buildCargo(2L, "Хімікати", CargoType.DANGEROUS, CargoStatus.ON_WAREHOUSE);
        when(dangerousCargoFactory.createCargo(anyString(), anyDouble(), anyDouble())).thenReturn(saved);
        when(cargoRepository.save(any(Cargo.class))).thenReturn(saved);

        Cargo result = cargoService.addCargo("Хімікати", 500.0, 3.0, CargoType.DANGEROUS);

        assertThat(result.getCargoType()).isEqualTo(CargoType.DANGEROUS);
    }

    @Test
    @DisplayName("❌ addCargo — негативний: порожня назва кидає виняток")
    void addCargo_blankName_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> cargoService.addCargo("", 100.0, 2.0, CargoType.STANDARD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Назва вантажу не може бути порожньою");

        verifyNoInteractions(cargoRepository);
    }

    @Test
    @DisplayName("❌ addCargo — негативний: null назва")
    void addCargo_nullName_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> cargoService.addCargo(null, 100.0, 2.0, CargoType.STANDARD))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("❌ addCargo — негативний: вага <= 0")
    void addCargo_zeroWeight_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> cargoService.addCargo("Вантаж", 0.0, 2.0, CargoType.STANDARD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Вага");
    }

    @Test
    @DisplayName("❌ addCargo — негативний: від'ємний об'єм")
    void addCargo_negativeVolume_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> cargoService.addCargo("Вантаж", 100.0, -1.0, CargoType.STANDARD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Об'єм");
    }

    @Test
    @DisplayName("❌ addCargo — негативний: null тип вантажу")
    void addCargo_nullType_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> cargoService.addCargo("Вантаж", 100.0, 2.0, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Тип вантажу");
    }


    @Test
    @DisplayName("✅ findById — позитивний: вантаж знайдено")
    void findById_existingId_returnsCargo() {
        Cargo cargo = buildCargo(5L, "Скло", CargoType.FRAGILE, CargoStatus.ON_WAREHOUSE);
        when(cargoRepository.findById(5L)).thenReturn(Optional.of(cargo));

        Cargo result = cargoService.findById(5L);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Скло");
    }

    @Test
    @DisplayName("❌ findById — негативний: вантаж не знайдено → EntityNotFoundException")
    void findById_nonExistingId_throwsEntityNotFoundException() {
        when(cargoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cargoService.findById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }


    @Test
    @DisplayName("✅ findAllOnWarehouse — повертає лише вантажі зі статусом ON_WAREHOUSE")
    void findAllOnWarehouse_returnsOnlyWarehouseCargos() {
        List<Cargo> warehouseList = List.of(
                buildCargo(1L, "A", CargoType.STANDARD, CargoStatus.ON_WAREHOUSE),
                buildCargo(2L, "B", CargoType.FRAGILE,  CargoStatus.ON_WAREHOUSE)
        );
        when(cargoRepository.findByStatus(CargoStatus.ON_WAREHOUSE)).thenReturn(warehouseList);

        List<Cargo> result = cargoService.findAllOnWarehouse();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> c.getStatus() == CargoStatus.ON_WAREHOUSE);
    }

    @Test
    @DisplayName("✅ findAllOnWarehouse — порожній список якщо склад порожній")
    void findAllOnWarehouse_emptyWarehouse_returnsEmptyList() {
        when(cargoRepository.findByStatus(CargoStatus.ON_WAREHOUSE)).thenReturn(List.of());

        List<Cargo> result = cargoService.findAllOnWarehouse();

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("✅ updateStatus — позитивний: статус оновлено")
    void updateStatus_validTransition_savesNewStatus() {
        Cargo cargo = buildCargo(1L, "Ящики", CargoType.STANDARD, CargoStatus.ON_WAREHOUSE);
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(cargoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cargo updated = cargoService.updateStatus(1L, CargoStatus.IN_TRANSIT);

        assertThat(updated.getStatus()).isEqualTo(CargoStatus.IN_TRANSIT);
        verify(cargoRepository).save(cargo);
    }

    @Test
    @DisplayName("❌ updateStatus — негативний: вантаж не існує")
    void updateStatus_nonExistentCargo_throwsEntityNotFoundException() {
        when(cargoRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cargoService.updateStatus(42L, CargoStatus.IN_TRANSIT))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
