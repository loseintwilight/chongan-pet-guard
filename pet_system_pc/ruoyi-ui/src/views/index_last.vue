<template>
  <div>
    <city-cascade
      :value="defaultCity"
      @input="handleInput"
      @change="handleCityChange"/>
  </div>
</template>

<script>
import CityCascade from "../components/CityCascade";

export default {
  components: {
    CityCascade
  },
  name: "index_last",
  data() {
    return {
      defaultCity: ['5aSp5r', '5biC6L', '5ZKM5b']
    }
  },
  methods: {
    handleCityChange(selectedData) {
      console.log(selectedData.label)
      console.log(selectedData.parent.label)
      console.log(selectedData.parent.parent.label)
    },
    handleInput(value) {
      this.defaultCity = value; // 更新绑定的值
      console.log(value)
    }
  }
}
</script>
