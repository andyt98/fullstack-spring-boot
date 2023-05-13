import {Button} from "@chakra-ui/react";
import SidebarWithHeader from "./components/shared/SideBar.jsx";
import {useEffect} from "react";
import {getCustomers} from "./services/client.js";

const App = () => {

    useEffect(() => {
        getCustomers()
            .then(res => {
                console.log(res)
            })
            .catch(err => {
                console.log(err)
    })
}, [])

return (
    <SidebarWithHeader>
        <Button> Click me </Button>
    </SidebarWithHeader>
)
}

export default App
